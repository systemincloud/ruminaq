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
class Logger(object):

    def __init__(self, pt_listener, task_id):
        self.pt_listener = pt_listener
        self.task_id = task_id

    def error(self, msg):
        self.pt_listener.log(self.task_id, "error", msg)

    def warn(self, msg):
        self.pt_listener.log(self.task_id, "warn", msg)

    def info(self, msg):
        self.pt_listener.log(self.task_id, "info", msg)

    def debug(self, msg):
        self.pt_listener.log(self.task_id, "debug", msg)

    def trace(self, msg):
        self.pt_listener.log(self.task_id, "trace", msg)
