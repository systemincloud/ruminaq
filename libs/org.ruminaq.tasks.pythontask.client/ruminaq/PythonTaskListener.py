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

class PythonTaskListener(object):

    def __init__(self, params):
        self.logger = Logger

    def external_data(self, nb):
        pass

    def sleep(self, l):
        pass

    def generator_pause(self):
        pass

    def generator_is_paused(self):
        return False

    def generator_resume(self):
        pass

    def generator_end(self):
        pass

    def exit_runner(self):
        pass

    def get_parameter(self, key):
        return ""

    def run_expression(self, expression):
        return ""

    def log(self):
        return self.logger
