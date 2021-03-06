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

class Complex32(Data):

    def __init__(self, values = None, dims = None):
        Data.__init__(self, values, dims)

    def get_bytes(self):
        real = array.array('f', [element.real for element in self.values])
        imag = array.array('f', [element.imag for element in self.values])
        real.byteswap()
        imag.byteswap()
        return real.tobytes() + imag.tobytes()

    def init_with_(self, bs, dims):
        floats = array.array('f', bs)
        floats.byteswap()
        halflen = int(len(floats)/2)
        real = floats[0:halflen]
        imag = floats[halflen:halflen*2]
        values = []
        for x in range(0, halflen):
            values.append(real[x] + 1j*imag[x])

        Data.__init__(self, values, dims)
