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
import codecs
import os

from setuptools import find_packages
from distutils.core import setup

here = os.path.abspath(os.path.dirname(__file__))

def read(*parts):
    with codecs.open(os.path.join(here, *parts), 'r') as fp:
        return fp.read()

long_description = read('README.rst')

setup(
    name = 'ruminaq',
    version = '1.0.2',
    packages = find_packages(),

    install_requires = [
        "thriftpy2>=0.3.9",
    ],

    include_package_data = True,

    test_suite='ruminaq.test.test_ruminaq',

    author = 'Marek Jagielski',
    author_email = 'marek.jagielski@gmail.com',

    description = 'python task api',
    long_description = long_description,
    license = 'Apache License version 2.0',
)
