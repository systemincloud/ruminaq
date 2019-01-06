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
from ruminaq.Logger import Logger

class PythonTask:

    def __init__(self, pt_listener = None, task_id = None, logger = None):
        self.pt_listener = pt_listener
        self.task_id = task_id
        if logger != None:
            self.logger = logger
        else:
            self.logger = Logger(None, None)

    def runner_start(self):
        pass

    def runner_stop(self):
        pass

    def execute(self, grp):
        pass

    def execute_async(self, async_in):
        pass

    def execute_ext_src(self):
        pass

    def generate(self):
        pass

    def external_data(self, n = 1):
        if self.pt_listener != None:
            self.pt_listener.externalData(self.task_id, n)

    def sleep(self, l):
        if self.pt_listener != None:
            self.pt_listener.sleep(self.task_id, l)

    def pause(self):
        if self.pt_listener != None:
            self.pt_listener.generatorPause(self.task_id)

    def paused(self):
        if self.pt_listener != None:
            return self.pt_listener.generatorIsPaused(self.task_id)
        return False

    def resume(self):
        if self.pt_listener != None:
            self.pt_listener.generatorResume(self.task_id)

    def end(self):
        if self.pt_listener != None:
            self.pt_listener.generatorEnd(self.task_id)

    def exit_runner(self):
        if self.pt_listener != None:
            self.pt_listener.exitRunner(self.task_id)

    def get_parameter(self, key):
        if self.pt_listener != None:
            return self.pt_listener.getParameter(self.task_id, key)
        return ""

    def run_expression(self, expression):
        if self.pt_listener != None:
            return self.pt_listener.runExpression(self.task_id, expression)
        return ""

    def log(self):
        return self.logger
