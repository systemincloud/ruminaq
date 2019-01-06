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
class _Parameter(object):

    def __init__(
            self,
            original_class,
            name,
            default_value = ''):
        self.original_class = original_class

    def __call__(self):
        return self.original_class

def parameter(
        original_class = None,
        name=None,
        default_value=''):

    if original_class:
        return _Parameter(
            original_class,
            name)
    else:
        def wrapper(original_class):
            return _Parameter(
                original_class,
                name,
                default_value)
        return wrapper
