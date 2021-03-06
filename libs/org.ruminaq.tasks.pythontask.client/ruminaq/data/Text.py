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

class Text(Data):

    def __init__(self, values = None, dims = None):
        Data.__init__(self, values, dims)

    def get_bytes(self):
        ret = b''
        for v in self.values:
            bv = v.encode("utf-8")
            l = array.array('i', [len(bv)])
            l.byteswap()
            ret = ret + l.tobytes() + bv
        return ret

    def init_with_(self, bs, dims):
        values = []
        k = 0
        while k < len(bs):
            l = array.array('i', bs[k : k + 4])
            l.byteswap()
            value = bs[k + 4 : k + 4 + l[0]].decode("utf-8")
            values.append(value)
            k = k + 4 + l[0]

        Data.__init__(self, values, dims)
