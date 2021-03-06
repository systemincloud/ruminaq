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
class Data:

    def __init__(self, values = None, dims = None):
        if dims is None:
            dims = [1]
        if  values is None:
            self.values = []
        elif isinstance(values, list):
            self.values = values
        else:
            self.values = [values]
        self.dims = dims

    @property
    def value(self):
        return self.values[0]
