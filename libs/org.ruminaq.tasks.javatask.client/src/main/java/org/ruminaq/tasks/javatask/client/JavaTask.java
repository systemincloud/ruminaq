/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client;

import org.slf4j.Logger;

/**
 *
 * @author Marek Jagielski
 */
public abstract class JavaTask {

	private JavaTaskListener jtListener;

	public void execute(int grp) {
	}

	public void executeAsync(InputPort asynchIn) {
	}

	public void executeExtSrc() {
	}

	public void generate() {
	}

	protected void externalData() {
		jtListener.externalData(1);
	}

	protected void externalData(int i) {
		jtListener.externalData(i);
	}

	protected void sleep(long l) {
		jtListener.sleep(l);
	}

	protected void pause() {
		jtListener.generatorPause();
	}

	protected boolean isPaused() {
		return jtListener.generatorIsPaused();
	}

	protected void resume() {
		jtListener.generatorResume();
	}

	protected void end() {
		jtListener.generatorEnd();
	}

	protected void exitRunner() {
		jtListener.exitRunner();
	}

	protected String getParameter(String key) {
		return jtListener.getParameter(key);
	}

	protected Object runExpression(String expression) {
		return jtListener.runExpression(expression);
	}

	protected Logger log() {
		return jtListener.log();
	}

	public void runnerStart() {
	}

	public void runnerStop() {
	}
}
