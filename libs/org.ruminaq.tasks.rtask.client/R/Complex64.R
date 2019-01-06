#' @include Data.R

Complex64 <- R6Class("Complex64",
  inherit = SicData,
  public = list(
    initialize = function(values) {
	    if(!missing(values)) super$initialize(values)
	  },
    get_bytes = function() {
      return(NULL)
    },
    init_with = function(bs, dims) {
    }
  )
)