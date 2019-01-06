"""
   Copyright 2018 Marek Jagielski

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
import thriftpy
import sys
from thriftpy.rpc import make_client
from thriftpy.rpc import make_server
import threading
import logging
from types import MethodType
from ruminaq.Globals import Globals
from ruminaq.Logger import Logger
from ruminaq.InputPort import InputPort
from ruminaq.OutputPort import OutputPort
from ruminaq.PythonTask import PythonTask
from thriftpy.transport import TTransportException, TException

LOCALHOST = "127.0.0.1"

def custom_serve(self):
    self.trans.listen()
    while not self.closed:
        try:
            client = self.trans.accept()
            t = threading.Thread(target=self.custom_handle, args=(client,))
            t.setDaemon(self.daemon)
            t.start()
        except KeyboardInterrupt:
            raise
        except Exception as x:
            logging.exception(x)

def custom_handle(self, client):
    itrans = self.itrans_factory.get_transport(client)
    otrans = self.otrans_factory.get_transport(client)
    iprot = self.iprot_factory.get_protocol(itrans)
    oprot = self.oprot_factory.get_protocol(otrans)
    try:
        while True:
            try:
                self.processor.process(iprot, oprot)
            except Exception:
                pass
            sys.stdout.flush()
            sys.stderr.flush()

    except TTransportException:
        pass
    except Exception as x:
        logging.exception(x)

    itrans.close()
    otrans.close()

class Engine(threading.Thread):

    def __init__(self, port, port_rev):
        threading.Thread.__init__(self)
        self.tasks = {}
        self.clients = {}
        self.running = False
        self.port = port
        try:
            self.__load_data()
            self.__connect_to_modeler(port)
            self.__run_server(port_rev)
            self.can_run = True
        except Exception:
            self.can_run = False

    def __load_data(self):
        t = Globals.thriftd.split(',')
        self.remotedata = thriftpy.load(t[0], module_name="remotedata_thrift", include_dirs=[t[1]])

    def __connect_to_modeler(self, port):
        t = Globals.thriftc.split(',')
        thriftc = thriftpy.load(t[0], module_name="runnersideserver_thrift", include_dirs=[t[1]])
        self.tc = thriftc.RunnerSideServer
        self.client = make_client(thriftc.RunnerSideServer, LOCALHOST, port)

    def __run_server(self, serverport):
        t = Globals.thrifts.split(',')
        thrifts = thriftpy.load(t[0], module_name="processsideserver_thrift", include_dirs=[t[1]])
        self.server = make_server(thrifts.ProcessSideServer, self, LOCALHOST, serverport)
        self.server.custom_serve = MethodType(custom_serve, self.server)
        self.server.custom_handle = MethodType(custom_handle, self.server)

    def run(self):
        if self.can_run == True:
            self.running = True
            self.server.custom_serve()

    def notify_modeler_running(self):
        self.client.runnerIsRunning()

    '''
        ********************************************
    '''

    def createTask(self, task_id,  impl_file):
        try:
            from importlib import import_module
            module = import_module(impl_file)
            task_cls = getattr(module, impl_file[impl_file.rfind(".")+1:])
            task = task_cls()
            while not isinstance(task, PythonTask):
                task = task()
            PythonTask.__init__(task, self, task_id, Logger(self, task_id))
            task.__init_ports__()

            for field in dir(task):
                if isinstance(getattr(task, field), InputPort):
                    getattr(task, field).pt_listener = self
                    getattr(task, field).task_id = task_id
                if isinstance(getattr(task, field), OutputPort):
                    getattr(task, field).pt_listener = self
                    getattr(task, field).task_id = task_id
            self.tasks[task_id] = task
            self.clients[task_id] = make_client(self.tc, LOCALHOST, self.port)
        except Exception as e:
            self.error_exit(e)

    def atomic(self, task_id):
        return self.tasks[task_id].atomic

    def generator(self, task_id):
        return self.tasks[task_id].generator

    def externalSource(self, task_id):
        return self.tasks[task_id].external_source

    def constant(self, task_id):
        return self.tasks[task_id].constant

    def runnerStart(self, task_id):
        try:
            self.tasks[task_id].runner_start()
        except Exception as e:
            self.error_exit(e)

    def runnerStop(self, task_id):
        try:
            self.tasks[task_id].runner_stop()
        except Exception as e:
            print(e)

    def execute(self, task_id, grp):
        try:
            self.tasks[task_id].execute(grp)
        except Exception as e:
            self.error_exit(e)

    def executeAsync(self, task_id, port_id):
        try:
            task = self.tasks[task_id]
            port = None
            for field in dir(task):
                f = getattr(task, field)
                if isinstance(f, InputPort) and f.name == port_id:
                    port = f
            self.tasks[task_id].execute_async(port)
        except Exception as e:
            self.error_exit(e)

    def executeExternalSrc(self, task_id):
        try:
            self.tasks[task_id].execute_ext_src()
        except Exception as e:
            self.error_exit(e)

    def generate(self, task_id):
        try:
            self.tasks[task_id].generate()
        except Exception as e:
            self.error_exit(e)

    '''
        ********************************************
    '''

    def external_ddata(self, task_id, i):
        self.clients[task_id].externalData(task_id, i)

    def sleep(self, task_id, l):
        self.clients[task_id].sleep(task_id, l)

    def generator_pause(self, task_id):
        self.clients[task_id].generatorPause(task_id)

    def generator_is_paused(self, task_id):
        return self.clients[task_id].generatorIsPaused(task_id)

    def generator_resume(self, task_id):
        self.clients[task_id].generatorResume(task_id)

    def generator_end(self, task_id):
        self.clients[task_id].generatorEnd(task_id)

    def exit_runner(self, task_id):
        self.clients[task_id].exitRunner(task_id)

    def get_parameter(self, task_id, key):
        return self.clients[task_id].getParameter(task_id, key)

    def run_expression(self, task_id, expression):
        return self.clients[task_id].runExpression(task_id, expression)

    def log(self, task_id, level, msg):
        self.client.log(task_id, level, msg)

    def get_data(self, task_id, portid, datatype):
        try:
            remotedata = self.clients[task_id].getData(task_id, portid, datatype.__name__)
            data = datatype()
            data.dims = remotedata.dims
            if isinstance(remotedata.buf, str):
                remotedata.buf = remotedata.buf.encode('utf-8')
            data.init_with_(remotedata.buf, remotedata.dims)
            return data
        except TException as exe:
            print(exe)
            sys.exit()

    def clean_queue(self, task_id, portid):
        self.clients[task_id].cleanQueue(task_id, portid)

    def put_data(self, task_id, portid, data):
        remotedata = self.remotedata.RemoteData()
        remotedata.type = type(data).__name__
        remotedata.buf  = data.get_bytes()
        remotedata.dims = data.dims
        self.clients[task_id].putData(task_id, portid, remotedata)

    '''
        ********************************************
    '''

    def error_exit(self, e):
        print(e)
        print("Python exit unexpectedly")
        sys.stdout.flush()
        sys.exit()
