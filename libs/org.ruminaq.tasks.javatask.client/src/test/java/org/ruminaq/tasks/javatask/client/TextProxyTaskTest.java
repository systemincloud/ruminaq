package org.ruminaq.tasks.javatask.client;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;

public class TextProxyTaskTest {

	@Test
	public void test() {
		JavaTaskInfo classAnn = TextProxyTask.class.getAnnotation(JavaTaskInfo.class);
		assertEquals(true, classAnn.atomic());
		for(Field f : TextProxyTask.class.getDeclaredFields()) {
			if(f.getName().equals("in")) {
				InputPortInfo ann = f.getAnnotation(InputPortInfo.class);
				assertEquals("In", ann.name());
			}
			if(f.getName().equals("ou")) {
				InputPortInfo ann = f.getAnnotation(InputPortInfo.class);
				assertEquals("Out", ann.name());
			}
		}
	}

}
