from sicpythontask.PythonTaskInfo import PythonTaskInfo
from sicpythontask.PythonTask import PythonTask
from sicpythontask.SicParameter import SicParameter
from sicpythontask.InputPort import InputPort
from sicpythontask.OutputPort import OutputPort
from sicpythontask.data.Bool import Bool

@PythonTaskInfo(generator=True, external_source=True)
@SicParameter(name="param")
class MyPythonTask(PythonTask):

    def __init_ports__(self):
        self.in1 = InputPort(name="in1", data_type=Bool)
        self.in2 = InputPort(name="in2", data_type=Bool, asynchronous=True)
        self.out = OutputPort(name="out", data_type=Bool)
     
    def execute_async(self, async_in):
        """ this will run when asynchronous port
            receive data """
        
    def execute_ext_src(self):
        """ this will run after internal
            request """
        
    def generate(self):
        """ this will run periodicaly """
        
    def execute(self, grp):
        """ this will run when all synchronous ports 
            from group receive data """
        
