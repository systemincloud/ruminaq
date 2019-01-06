#' @include Data.R

Decimal <- R6Class("Decimal",
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
