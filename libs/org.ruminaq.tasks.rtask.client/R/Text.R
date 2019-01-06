#' @include Data.R

Text <- R6Class("Text",
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
