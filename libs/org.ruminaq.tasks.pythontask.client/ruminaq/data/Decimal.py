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
from decimal import Decimal as D
from ruminaq.data.Data import Data
import decimal

class Decimal(Data):

    FIXED_INTS = 4

    def __init__(self, values = None, dims = None):
        Data.__init__(self, values, dims)

    def get_bytes(self):
        ret = ''
        for v in self.values:
            ret = ret + str(v) +  ' '
        return ret.encode('utf-8')

    def init_with_(self, bs, dims):
        values = []
        k = 0
        while k < len(bs):
            ints = array.array('i', bs[k : k + 4*Decimal.FIXED_INTS])
            ints.byteswap()
            if ints[1] > decimal.getcontext().prec:
                decimal.getcontext().prec = ints[1]
            value = array.array('I', bs[k + 4*Decimal.FIXED_INTS : k + 4*Decimal.FIXED_INTS + ints[3]])
            value.byteswap()
            d = D(0)
            l = 0
            for v in reversed(value):
                d = d + D(v)*(D(4294967296)**l)
                l = l + 1
            d = ints[0]*d
            d = d / (D(10)**ints[2])
            values.append(d)
            k = k + 4*Decimal.FIXED_INTS + ints[3]

        Data.__init__(self, values, dims)
