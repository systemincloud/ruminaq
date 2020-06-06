package org.ruminaq.tasks.pythontask.wizards

import org.ruminaq.tasks.userdefined.model.userdefined.Module
import java.util.LinkedList
import java.util.HashSet

class CodeGenerator {

  def static generate(Module module) {
    val conf        = #[module.atomic,
    	                module.generate,
    	                module.externalSource,
    	                module.constant,
    	                module.onlyLocal]
    val defaultConf = #[true,
    	                false,
    	                false,
    	                false,
    	                false]

   	var showConf = new LinkedList
   	for(i : 0 .. conf.size - 1) showConf.add(conf.get(i) != defaultConf.get(i))

  	var datas = new HashSet
   	for(i : module.inputs)  datas.add(i.dataType)
   	for(o : module.outputs) datas.add(o.dataType)

  	'from sicpythontask.PythonTaskInfo import PythonTaskInfo\n' +
    'from sicpythontask.PythonTask import PythonTask\n' +
    '''«IF module.parameters.size > 0»from sicpythontask.SicParameter import SicParameter«'\n'»«ENDIF»«
      »«IF module.inputs.size > 0»from sicpythontask.InputPort import InputPort«'\n'»«ENDIF»«
      »«IF module.outputs.size > 0»from sicpythontask.OutputPort import OutputPort«'\n'»«ENDIF»«
      »«FOR d : datas»«
      	»from «module.importPrefix.get(d)»«d» import «d»«'\n'»«
      »«ENDFOR»''' + '\n' +

  	'''@PythonTaskInfo''' +

	switch showConf.filter[ it == true ].size {
		case 0: "\n"
		default:
			'''(«FOR i : 0 .. showConf.size - 1»«
				»«IF showConf.get(i)»«
					»«IF i == 0»atomic=«module.atomic.toString.toFirstUpper»«ENDIF»«
					»«IF i == 1»generator=«module.generate.toString.toFirstUpper»«ENDIF»«
					»«IF i == 2»external_source=«module.externalSource.toString.toFirstUpper»«ENDIF»«
					»«IF i == 3»constant=«module.constant.toString.toFirstUpper»«ENDIF»«
					»«IF i == 4»only_local=«module.onlyLocal.toString.toFirstUpper»«ENDIF»«
					»«IF i < 4 && showConf.subList(i + 1,showConf.size).exists[it == true]», «ENDIF»«
				»«ENDIF»«
				»«ENDFOR»)
				'''
	}

	+

	'''
	   «FOR p : module.parameters»
	   @SicParameter(name="«p.name»"«IF p.defaultValue != ""», default_value="«p.defaultValue»"«ENDIF»)
	   «ENDFOR»
	'''

    +

  '''
    class «module.name»(PythonTask):

    «IF module.inputs.size > 0 || module.outputs.size > 0»
    «"    "»def __init_ports__(self):«
    "\n"
      »«FOR in : module.inputs»«
        	»«var showInConf = new LinkedList<Boolean>»«
        	»«showInConf.addLast(true)»«
        	»«showInConf.addLast(!in.dataType    .equals("Control"))»«
        	»«showInConf.addLast(!in.asynchronous.equals(false))»«
        	»«showInConf.addLast(!in.group       .equals(-1))»«
        	»«showInConf.addLast(!in.hold        .equals(false))»«
        	»«showInConf.addLast(!in.queue       .equals(1))»«
            »«"    "»«"    self."»«in.getName().toLowerCase.replace(" ", "_").trim» = InputPort(«
            »«FOR i : 0 .. showInConf.size - 1»«
				»«IF showInConf.get(i)»«
					»«IF i == 0»name="«in.getName()»"«ENDIF»«
					»«IF i == 1»data_type=«in.dataType»«ENDIF»«
					»«IF i == 2»asynchronous=«in.asynchronous.toString.toFirstUpper»«ENDIF»«
					»«IF i == 3»group=«in.group»«ENDIF»«
					»«IF i == 4»hold=«in.hold.toString.toFirstUpper»«ENDIF»«
					»«IF i == 5»queue=«in.queue»«ENDIF»«
					»«IF i < 5 && showInConf.subList(i + 1,showInConf.size).exists[it == true]», «ENDIF»«
			    »«ENDIF»«
			»«ENDFOR»)«
			"\n"
      »«ENDFOR»«
      »«FOR out : module.outputs»«
        	»«var showOutConf = new LinkedList<Boolean>»«
        	»«showOutConf.addLast(true)»«
        	»«showOutConf.addLast(!out.dataType.equals("Control"))»«
            »«"    "»«"    self."»«out.getName().toLowerCase.replace(" ", "_").trim» = OutputPort(«
            »«FOR i : 0 .. showOutConf.size - 1»«
				»«IF showOutConf.get(i)»«
					»«IF i == 0»name="«out.getName()»"«ENDIF»«
					»«IF i == 1»data_type=«out.dataType»«ENDIF»«
					»«IF i < 2 && showOutConf.subList(i + 1,showOutConf.size).exists[it == true]», «ENDIF»«
			    »«ENDIF»«
			»«ENDFOR»)«
			"\n"
      »«ENDFOR»

     «ENDIF»
    «IF module.runnerStart»
      «"    "»def runner_start(self):
              """ this will run at the begining
                  of Runner """

    «ENDIF»
    «IF module.executeAsync»
      «"    "»def execute_async(self, async_in):
              """ this will run when asynchronous port
                  receive data """

    «ENDIF»
    «IF module.executeExtSrc»
      «"    "»def execute_ext_src(self):
              """ this will run after internal
                  request """

    «ENDIF»
    «IF module.generate»
      «"    "»def generate(self):
              """ this will run periodicaly """

    «ENDIF»
    «IF module.execute»
      «"    "»def execute(self, grp):
              """ this will run when all synchronous ports
                  from group receive data """

    «ENDIF»
    «IF module.runnerStop»
      «"    "»def runner_stop(self):
              """ this will run at the end
                  of Runner """

    «ENDIF»
  '''
	}
}
