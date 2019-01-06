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
from ruminaq.PythonTask import PythonTask
from inspect import isclass

class _PythonTaskInfo(object):

    def __init__(self, original_class, atomic=True,
                                       generator=False,
                                       external_source=False,
                                       constant=False,
                                       only_local=False):

        while not (isclass(original_class) and issubclass(original_class, PythonTask)):
            original_class = original_class.original_class

        self.original_class = original_class
        self.original_class.atomic = atomic
        self.original_class.generator = generator
        self.original_class.external_source = external_source
        self.original_class.constant = constant
        self.original_class.only_local = only_local

    def __call__(self):
        return self.original_class

def python_task_info(
        original_class = None,
        atomic=True,
        generator=False,
        external_source=False,
        constant=False,
        only_local=False):

    if original_class:
        return _PythonTaskInfo(original_class)
    else:
        def wrapper(original_class):
            return _PythonTaskInfo(
                original_class,
                atomic,
                generator,
                external_source,
                constant,
                only_local)
        return wrapper
