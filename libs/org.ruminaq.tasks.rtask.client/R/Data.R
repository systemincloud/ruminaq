
SicData <- R6Class("SicData",
  public = list(
	values = NA,
    initialize = function(values) {
	    if(typeof(values) == "array") self$values <- values
	    else self$values <- array(c(values))
	  }
  ),
  active = list(
	  value = function(v) {
	    if (missing(v)) self$values[1]
	    else self$values[1] <- v
	  }
  )
)
