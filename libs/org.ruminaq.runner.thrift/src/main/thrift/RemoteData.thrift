namespace java org.ruminaq.runner.thrift
namespace py ruminaq

struct RemoteData {
  1: string    type,
  2: list<i32> dims,
  3: binary    buf
}
