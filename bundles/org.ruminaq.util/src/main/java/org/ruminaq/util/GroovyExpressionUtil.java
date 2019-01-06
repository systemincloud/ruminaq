/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.util;

import java.math.BigDecimal;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyExpressionUtil {

	public static boolean isOneDimIntegerAlsoGV(String value) {
		if(GlobalUtil.isGlobalVariable(value)) return true;
		String expression = value.replaceAll(GlobalUtil.GV, "1");
		if(expression.contains(";")) return false;
		GroovyShell shell = new GroovyShell(new Binding());
		Object result = shell.evaluate(expression);
		if(result instanceof Integer) return true;
		return false;
	}

	public static boolean isOneDimNumericAlsoGV(String value) {
		if(GlobalUtil.isGlobalVariable(value)) return true;
		String expression = value.replaceAll(GlobalUtil.GV, "1");
		if(expression.contains(";")) return false;
		GroovyShell shell = new GroovyShell(new Binding());
		Object result = shell.evaluate(expression);
		if(result instanceof Integer ||
		   result instanceof BigDecimal) return true;
		return false;
	}

	public static Object evaluate(String expression) {
		GroovyShell shell = new GroovyShell(new Binding());
		return shell.evaluate(expression);
	}
}
