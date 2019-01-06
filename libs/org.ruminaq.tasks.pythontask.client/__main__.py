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
import sys
import platform
import time
from ruminaq.Globals import Globals

def main(argv):
    Globals.py_syntax = int(platform.python_version()[:1])
    Globals.tcp_port = int(argv[0])
    Globals.tcp_port_rev = int(argv[1])
    Globals.path = argv[2]
    Globals.extlib = argv[3]
    Globals.thriftc = argv[4]
    Globals.thrifts = argv[5]
    Globals.thriftd = argv[6]

    for p in Globals.path.split(';'):
        if p not in sys.path and p != '':
            sys.path.insert(0, p)
    for p in Globals.extlib.split(';'):
        if p not in sys.path and p != '':
            sys.path.insert(0, p)

    from ruminaq.Engine import Engine

    engine = Engine(Globals.tcp_port, Globals.tcp_port_rev)

    if engine.can_run == True:
        engine.start()
        while engine.running == False:
            time.sleep(1)
        engine.notify_modeler_running()
        engine.join()

if __name__ == "__main__":
    main(sys.argv[1:])
