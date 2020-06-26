package org.ruminaq.tasks.rtask.wizards

import org.ruminaq.eclipse.usertask.model.userdefined.Module
import java.util.LinkedList
import java.util.HashSet

class CodeGenerator {

  def static generate(Module module) {
    val conf        = #[module.atomic,
    	                module.generate,
    	                module.externalSource,
    	                module.constant]
    val defaultConf = #[true,
    	                false,
    	                false,
    	                false,
    	                false]

    val methods = #[module.runnerStart,
                    module.executeAsync,
    	            module.executeExtSrc,
    	            module.generate,
    	            module.execute,
    	            module.runnerStop]

   	var showConf = new LinkedList
   	for(i : 0 .. conf.size - 1) showConf.add(conf.get(i) != defaultConf.get(i))

  	var libs = new HashSet
  	libs.add('sicrtask')
   	for(i : module.inputs)  libs.add(module.importPrefix.get(i.dataType))
   	for(o : module.outputs) libs.add(module.importPrefix.get(o.dataType))

  '''«"\n"»«
  	»library(R6)«"\n"»«
    »«FOR l : libs»library(«l»)«"\n"»«ENDFOR»«"\n"»«
    »«module.name» <- R6Class("«module.name»",«"\n"»«
  	  »  inherit = RTask,«"\n"»«
  	  »  public = list(«"\n"»«
  	  »    rtaskinfo = RTaskInfo$new(«
	»«FOR i : 0 .. showConf.size - 1»«
        »«IF showConf.get(i)»«
		    »«IF i == 0»atomic = «module.atomic.toString.toUpperCase»«ENDIF»«
			»«IF i == 1»generator = «module.generate.toString.toUpperCase»«ENDIF»«
			»«IF i == 2»external_source = «module.externalSource.toString.toUpperCase»«ENDIF»«
			»«IF i == 3»constant = «module.constant.toString.toUpperCase»«ENDIF»«
			»«IF i < 3 && showConf.subList(i + 1,showConf.size).exists[it == true]», «ENDIF»«
	    »«ENDIF»«
	»«ENDFOR»),«"\n"»«
	»«IF module.parameters.size > 0»«
	  »    sicparameters = list(«"\n"»«
	    »«FOR i : 0 .. module.parameters.size - 1 »«
	      val p = module.parameters.get(i)
	      »«"      "»SicParameter$new(name = "«p.name»"«IF p.defaultValue != ""», default_value = "«p.defaultValue»"«ENDIF»)«
	      »«IF i < module.parameters.size - 1»,«ENDIF»«"\n"»«
	    »«ENDFOR»«
	  »    ),«"\n"»«
	»«ENDIF»«
    »«IF module.inputs.size > 0 || module.outputs.size > 0»«
      »«FOR in : module.inputs  »«"    "»«in.getName() .toLowerCase.replace(" ", "_").trim» = NA,«"\n"»«ENDFOR»«
      »«FOR out : module.outputs»«"    "»«out.getName().toLowerCase.replace(" ", "_").trim» = NA,«"\n"»«ENDFOR»«
      »«"    "»initialize_ports = function() {«"\n"»«
      »«FOR in : module.inputs»«
        	»«var showInConf = new LinkedList<Boolean>»«
        	»«showInConf.addLast(true)»«
        	»«showInConf.addLast(!in.dataType    .equals("Control"))»«
        	»«showInConf.addLast(!in.asynchronous.equals(false))»«
        	»«showInConf.addLast(!in.group       .equals(-1))»«
        	»«showInConf.addLast(!in.hold        .equals(false))»«
        	»«showInConf.addLast(!in.queue       .equals(1))»«
            »«"      "»self$«in.getName().toLowerCase.replace(" ", "_").trim» <- InputPort$new(«
            »«FOR i : 0 .. showInConf.size - 1»«
			    »«IF showInConf.get(i)»«
					»«IF i == 0»name = "«in.getName()»"«ENDIF»«
					»«IF i == 1»data_type = "«in.dataType»"«ENDIF»«
					»«IF i == 2»asynchronous = «in.asynchronous.toString.toUpperCase»«ENDIF»«
					»«IF i == 3»group = «in.group»«ENDIF»«
					»«IF i == 4»hold = «in.hold.toString.toUpperCase»«ENDIF»«
					»«IF i == 5»queue = «in.queue»«ENDIF»«
					»«IF i < 5 && showInConf.subList(i + 1,showInConf.size).exists[it == true]», «ENDIF»«
			    »«ENDIF»«
			»«ENDFOR»)«"\n"»«
	  »«ENDFOR»«
      »«FOR out : module.outputs»«
        	»«var showOutConf = new LinkedList<Boolean>»«
        	»«showOutConf.addLast(true)»«
        	»«showOutConf.addLast(!out.dataType.equals("Control"))»«
            »«"      "»self$«out.getName().toLowerCase.replace(" ", "_").trim» <- OutputPort$new(«
            »«FOR i : 0 .. showOutConf.size - 1»«
				»«IF showOutConf.get(i)»«
					»«IF i == 0»name = "«out.getName()»"«ENDIF»«
					»«IF i == 1»data_type = "«out.dataType»"«ENDIF»«
					»«IF i < 2 && showOutConf.subList(i + 1,showOutConf.size).exists[it == true]», «ENDIF»«
			    »«ENDIF»«
			»«ENDFOR»)«"\n"»«
      »«ENDFOR»«"    "»},«"\n"»«
    »«ENDIF»«
    »«"    "»initialize = function() {«"\n"»«
		»«IF module.inputs.size > 0 || module.outputs.size > 0»«"      "»self$initialize_ports()«"\n"»«ENDIF»«
	»«"    "»}«IF methods.subList(0, methods.size).exists[it == true]»,«ENDIF»«"\n"»«
    »«IF methods.get(0)»
    «"    "»runner_start = function() {
          # this will run at the begining of Runner
        }«IF methods.subList(1, methods.size).exists[it == true]»,«ENDIF»
    «ENDIF»«
    »«IF methods.get(1)»
    «"    "»execute_async = function(async_in) {
          # this will run when asynchronous port receive data
        }«IF methods.subList(2, methods.size).exists[it == true]»,«ENDIF»
    «ENDIF»«
    »«IF methods.get(2)»
    «"    "»execute_ext_src = function() {
          # this will run after internal request
        }«IF methods.subList(3, methods.size).exists[it == true]»,«ENDIF»
    «ENDIF»«
    »«IF methods.get(3)»
    «"    "»generate = function() {
          # this will run periodicaly
        }«IF methods.subList(4, methods.size).exists[it == true]»,«ENDIF»
    «ENDIF»«
    »«IF methods.get(4)»
    «"    "»execute = function(grp) {
          # this will run when all synchronous ports from group receive data
        }«IF methods.subList(5, methods.size).exists[it == true]»,«ENDIF»
    «ENDIF»«
    »«IF methods.get(5)»
    «"    "»runner_stop = function() {
          # this will run at the end of Runner
        }
    «ENDIF»
  ),
  private = list(
  )
)
  '''
	}
}
