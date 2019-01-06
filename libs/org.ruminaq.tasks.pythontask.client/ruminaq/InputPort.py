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
from ruminaq.data.Control import Control

class InputPort:


    def __init__(self, name, data_type = None, asynchronous = False, group = -1, hold = False, queue = -1):
        if data_type is None:
            data_type = [ Control ]
        self.name = name
        self.data_type = data_type
        self.asynchronous = asynchronous
        self.group = group
        self.hold = hold
        self.queue = queue
        self.pt_listener = None
        self.task_id = None

    def get_data(self, datatype):
        if self.pt_listener != None:
            return self.pt_listener.getData(self.task_id, self.name, datatype)

    def clean_queue(self):
        if self.pt_listener != None:
            self.pt_listener.cleanQueue(self.task_id, self.name)
