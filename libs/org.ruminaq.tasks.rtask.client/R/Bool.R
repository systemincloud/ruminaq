#' @include Data.R

Bool <- R6Class("Bool",
  inherit = SicData,
  public = list(
    initialize = function(values) {
	    if(!missing(values)) super$initialize(values)
    },
    get_bytes = function() {
#      values = array.array('i', self.values)
#      values.byteswap()
#      return values.tobytes()
    },
    init_with = function(bs, dims) {
#      ints = array.array('i', bs)
#      ints.byteswap()
#      Data.__init__(self, ints.tolist(), dims)
    }
  )
)
