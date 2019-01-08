namespace java org.ruminaq.runner.thrift
namespace py ruminaq

/*
 RunnerSideServer
*/

include "RemoteData.thrift"

typedef i32 int
typedef i64 long

service RunnerSideServer
{
  void runnerIsRunning(),

  void externalData(1:string taskid, 2:int i),
  void sleep(1:string taskid, 2:long l),
  void generatorPause(1:string taskid),
  bool generatorIsPaused(1:string taskid),
  void generatorResume(1:string taskid),
  void generatorEnd(1:string taskid),
  void exitRunner(1:string taskid),
  string getParameter(1:string taskid, 2:string key),
  string runExpression(1:string taskid, 2:string expression),
  void log(1:string taskid, 2:string level, 3:string message),

  // InputPort
  RemoteData.RemoteData getData(1:string taskid, 2:string portid, 3:string datatype),
  void cleanQueue(1:string taskid, 2:string portid),

  // OutputPort
  void putData(1:string taskid, 2:string portid, 3:RemoteData.RemoteData data)
}
