package org.ruminaq.tools.img

import groovy.json.JsonSlurper

class PrepareIcons {

	def execute(basedir) {
		File icons_xcf = new File(basedir + "/icons_xcf")
		def icons = new File(basedir + "/icons")
		def html_img  = new File(basedir + "/html/img")

		def conf = Eval.me(new File(icons_xcf.absolutePath + '/icons.groovy').text)
		println conf

		println "**********************************************"

		if (!icons.exists()) {
			icons.mkdir()
		}
		if (!html_img.exists()) {
			icons.mkdir()
		}

		def proc

		def borderXcf = new File("/tmp/template.palette.border.xcf")
		def borderPng = new File("/tmp/template.palette.border.png")
		new FileOutputStream(borderXcf.absolutePath).write(PrepareIcons.class.getResourceAsStream("template.palette.border.xcf").getBytes())
		proc = """convert -geometry x22 ${borderXcf.absolutePath} ${borderPng.absolutePath}""".execute()
		proc.waitFor()

		def whiteXcf = new File("/tmp/template.palette.white.xcf")
		def whitePng = new File("/tmp/template.palette.white.png")
		new FileOutputStream(whiteXcf.absolutePath).write(PrepareIcons.class.getResourceAsStream("template.palette.white.xcf").getBytes())
		proc = """convert -geometry x22 ${whiteXcf.absolutePath} ${whitePng.absolutePath}""".execute()
		proc.waitFor()

		conf.each { String file, List cmds ->
			def fileName = file.replace(".xcf", "")
			def xcf  = new File(icons_xcf.absolutePath + '/' + file)
			def png  = new File('/tmp/' + fileName + ".png")
			def process
			process = """xcf2png ${xcf.absolutePath} -o ${png.absolutePath} -A""".execute()
			process.waitFor()
			if(cmds.contains('diagram')) {
				process = """convert -geometry x30 ${png.absolutePath} ${icons.absolutePath}/diagram.${fileName}.png""".execute() ; process.waitFor()
			}
			if(cmds.contains('palette-border')) {
				def tmp = new File("/tmp/forPalette.png")
				process = """convert   -geometry x15   ${png.absolutePath} ${tmp.absolutePath}""".execute() ; process.waitFor()
				process = """composite -gravity center ${tmp.absolutePath} ${borderPng.absolutePath} ${icons.absolutePath}/palette.${fileName}.png""".execute() ; process.waitFor()
				tmp.delete()
			}
			if(cmds.contains('palette-white')) {
				def tmp = new File("/tmp/forPalette.png")
				process = """convert   -geometry x15   ${png.absolutePath} ${tmp.absolutePath}""".execute() ; process.waitFor()
				process = """composite -gravity center ${tmp.absolutePath} ${whitePng.absolutePath} ${icons.absolutePath}/palette.${fileName}.png""".execute() ; process.waitFor()
				tmp.delete()
			}
			if(cmds.contains('palette')) {
				process = """convert -geometry x22 ${png.absolutePath} ${icons.absolutePath}/palette.${fileName}.png""".execute() ; process.waitFor()
			}
			if(cmds.contains('icon'))    {
				process = """convert -geometry x30 ${png.absolutePath} ${html_img.absolutePath}/icon.png""".execute() ; process.waitFor()
			}
      if(cmds.contains('target'))    {
        process = """convert -geometry x22 ${png.absolutePath} ${html_img.absolutePath}/target.${fileName}.png""".execute() ; process.waitFor()
      }

			png.delete()
		}

		borderXcf.delete()
		borderPng.delete()
		whiteXcf.delete()
		whitePng.delete()

		println "**********************************************"
	}
}
