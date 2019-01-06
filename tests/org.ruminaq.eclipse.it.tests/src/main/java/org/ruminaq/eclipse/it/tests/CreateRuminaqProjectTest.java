/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.it.tests;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqProjectTest {

    private static SWTWorkbenchBot bot;

    @BeforeClass
    public static void initBot() {
        bot = new SWTWorkbenchBot();
    }

    @AfterClass
    public static void afterClass() {
        bot.resetWorkbench();
    }

    @Test
    public void testCreateProjectTest() {
        bot.menu("File")
            .menu("New")
            .menu("Project...")
            .click();

        bot.tree()
            .getTreeItem("Ruminaq")
            .expand();

        bot.tree()
            .getTreeItem("Ruminaq")
            .getNode("Ruminaq Project")
            .select();

        bot.button("Next >")
            .click();

        bot.textWithLabel("&Project name:")
            .setText("test" + RandomStringUtils.randomAlphabetic(5));

        bot.button("Finish")
            .click();

        SWTBotShell shellOpenPerpeitve = bot.shell("Open Associated Perspective?");
        shellOpenPerpeitve.activate();
        bot.button("Open Perspective")
            .click();

        bot.waitUntil(shellCloses((shellOpenPerpeitve)));
    }
}
