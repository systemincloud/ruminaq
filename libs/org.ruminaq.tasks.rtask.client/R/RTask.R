
library(R6)

InputPort <- R6Class("InputPort",
  public = list(
    name = NA,
    data_type = NA,
    asynchronous = NA,
    group = NA,
    hold = NA,
    queue = NA,
	  rtListener = NA,
    initialize = function(name, data_type = "SicControl", asynchronous = FALSE, group = -1, hold = FALSE, queue = 1) {
      self$name <- name
      self$data_type <- data_type
      self$asynchronous <- asynchronous
      self$group <- group
      self$hold <- hold
      self$queue <- queue
    },
    get_data = function(datatype) {
	    data <- self$rtListener$get_data(self$name, datatype)
	    return(data)
    },
    clean_queue = function() {
	    self$rtListener$clean_queue(self$name)
    }
  )
)

OutputPort <- R6Class("OutputPort",
  public = list(
    name = NA,
    data_type = NA,
	  rtListener = NA,
    initialize = function(name, data_type = "SicControl") {
      self$name <- name
      self$data_type <- data_type
    },
    put_data = function(data) {
		  self$rtListener$put_data(self$name, data)
    }
  )
)

RTaskInfo <- R6Class("RTaskInfo",
  public = list(
    atomic = NA,
    generator = NA,
    external_source = NA,
    constant = NA,
    only_local = NA,
    initialize = function(atomic = TRUE,
                          generator = FALSE,
                          external_source = FALSE,
                          constant = FALSE,
                          only_local = FALSE) {
      self$atomic = atomic
      self$generator = generator
      self$external_source = external_source
      self$constant = constant
      self$only_local = only_local
    }
  )
)

SicParameter <- R6Class("SicParameter",
  public = list(
    name = NA,
    default_value = NA,
    initialize = function(name,
                          default_value = '') {
      self$name = name
      self$default_value = default_value
    }
  )
)

RTask <- R6Class("RTask",
  public = list(
    rtaskinfo = NA,
    sicparameters = NA,
	  logger = NA,
	  rtListener = NA,
	  initialize = function() { },
	  copyRtListener = function() {
	    for(name in ls(self, all.names = TRUE)) {
		    el <- self[[name]]
		    if(is.environment(el) && !is.null(el)) {
		      cl = class(el)
		      if(identical(cl[2], 'R6')) {
		        if(identical(cl[1], 'InputPort') || identical(cl[1], 'OutputPort')) {
			        el$rtListener <- self$rtListener
			      }
		      }
	      }
	    }
    },

    runner_start = function() { },
    runner_stop = function() { },
    execute = function(grp) { },
    execute_async = function(async_in) { },
    execute_ext_src = function() { },
    generate = function() { },

    external_data = function(n = 1) {
	    self$rtListener$external_data(n)
    },
	  sleep = function(l) {
	    self$rtListener$sleep(n)
	  },
    pause = function() {
	    self$rtListener$generatorPause()
	  },
    paused = function() {
	    self$rtListener$generatorIsPaused()
	  },
    resume = function() {
	    self$rtListener$generatorResume()
	  },
    end = function() {
	    self$rtListener$generatorEnd()
	  },
    exit_runner = function() {
	    self$rtListener$exitRunner()
	  },
    get_parameter = function(key) {
	    self$rtListener$getParameter(key)
	  },
    run_expression = function(expression) {
	    self$rtListener$runExpression(expression)
    },
    log = function() {
	    self$logger
	  }
  )
)

Logger <- R6Class("Logger",
  public = list(
    rtListener = NA,
	  initialize = function() { },
	  error = function(msg) {
	    self$rtListener$log("error", msg)
	  },
	  warn = function(msg) {
	    self$rtListener$log("warn", msg)
	  },
	  info = function(msg) {
	    self$rtListener$log("info", msg)
	  },
	  debug = function(msg) {
	    self$rtListener$log("debug", msg)
	  },
	  trace = function(msg) {
	    self$rtListener$log("trace", msg)
	  }
  )
)

RTListener <- R6Class("RTListener",
  public = list(
	  client = NA,
	  taskid = NA,
    initialize = function(taskid) {
	    self$taskid <- taskid
    },
    get_data = function(portname, datatype) {
      rdata = self$client$getData(self$taskid, portname, datatype)
      data <- eval(parse(text=paste(datatype, "$new()")))
      data$init_with(rdata$buf, rdata$dims)
      return(data)
    },
    clean_queue = function(portname) {
      self$client$cleanQueue(self$taskid, portname)
    },
    put_data = function(portname, data) {
      rdata = self.remotedata.RemoteData()
      rdata.type = data$name
      rdata.buf  = data$get_bytes()
      rdata.dims = data$dims
      self$client$putData(self$taskid, portname, rdata)
    },
	  external_data = function(n) {
      self$client$externalData(self$taskid, i)
	  },
	  sleep = function(l) {
      self$client$sleep(self$taskid, l)
	  },
	  generatorPause = function() {
      self$client$generatorPause(self$taskid)
    },
	  generatorIsPaused = function() {
      return(self$clients$.generatorIsPaused(self$taskid))
    },
	  generatorResume = function() {
      self$client$generatorResume(self$taskid)
    },
	  generatorEnd = function() {
      self$client$generatorEnd(self$taskid)
    },
	  exitRunner = function() {
      self$client$exitRunner(self$taskid)
    },
	  getParameter = function(key) {
      return(self$client$getParameter(self$taskid, key))
    },
	  runExpression = function(expression) {
      return(self$client$runExpression(self$taskid, expression))
    },
	  log = function(level, msg) {
      self$client$log(self$taskid, level, msg)
	  }
  )
)