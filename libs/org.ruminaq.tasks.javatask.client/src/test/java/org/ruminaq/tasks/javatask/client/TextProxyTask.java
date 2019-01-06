package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.InputPort;
import org.ruminaq.tasks.javatask.client.JavaTask;
import org.ruminaq.tasks.javatask.client.OutputPort;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.data.Text;

@JavaTaskInfo
public class TextProxyTask extends JavaTask {

	@InputPortInfo(name="In", dataType=Text.class)
	public InputPort in;
	
	@OutputPortInfo(name="Out", dataType=Text.class)
	public OutputPort out;
	
	@Override
	public void execute(int grp) {
		String value = in.getData(Text.class).getValues().get(0);
		out.putData(new Text(value));
	}

	@Override
	public void executeAsync(InputPort asynchIn) { }

}
