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
import array
from ruminaq.data.Data import Data

class Bool(Data):

    def __init__(self, values = None, dims = None):
        Data.__init__(self, values, dims)

    def get_bytes(self):
        return array.array('b', self.values).tobytes()

    def init_with_(self, bs, dims):
        ints = array.array("i")
        ints.frombytes(bs)
        bools = list(map(lambda x:bool(x),ints))
        Data.__init__(self, bools, dims)
