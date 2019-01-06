========
Ruminaq
========

Ruminaq python package is part of Ruminaq project https://github.com/systemincloud/ruminaq.
This package is an api for writing custom tasks in python.


Installation
============

Install with pip.

.. code:: bash

    $ pip install ruminaq


Code Demo
=========

Define a custom task as follow:

.. code:: python

	@python_task_info(generator=True, external_source=True)
	@parameter(name="param")
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
