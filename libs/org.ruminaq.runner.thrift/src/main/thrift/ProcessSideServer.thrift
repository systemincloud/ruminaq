namespace java org.ruminaq.runner.thrift
namespace py ruminaq

/*
 ProcessSideServer
*/

typedef i32 int
typedef i64 long

service ProcessSideServer
{
  void createTask(1:string id, 2:string implFile),

  bool atomic(1:string id),
  bool generator(1:string id),
  bool externalSource(1:string id),
  bool constant(1:string id),

  void runnerStart(1:string id),
  void runnerStop(1:string id),

  void execute(1:string id, 2:int grp),
  void executeAsync(1:string id, 2:string portId),
  void executeExternalSrc(1:string id),
  void generate(1:string id)
}
