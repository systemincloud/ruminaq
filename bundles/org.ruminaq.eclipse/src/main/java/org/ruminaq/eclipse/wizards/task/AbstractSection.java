/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Common class for componenets in UserDefinedTask creation psage.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractSection extends Group {

  protected static final int TWO_COLUMNS = 2;
  protected static final int FIVE_COLUMNS = 5;

  protected CreateUserDefinedTaskPage userDefinedTaskPage;

  protected AbstractSection(CreateUserDefinedTaskPage page, Composite parent,
      int style) {
    super(parent, style);
    this.userDefinedTaskPage = page;
    initLayout();
    initComponents();
    initActions();
  }

  @Override
  protected void checkSubclass() {
    // allow subclass
  }

  protected abstract void initLayout();

  protected abstract void initComponents();

  protected abstract void initActions();
}
