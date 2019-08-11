/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.runner.dirmi.DirmiServer;
import org.ruminaq.runner.util.Util;
import org.ruminaq.tasks.api.IView;
import org.ruminaq.tasks.console.impl.ConsoleIService;
import org.ruminaq.tasks.console.impl.ConsoleViewService;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleType;

import swing2swt.layout.BorderLayout;

public class ConsoleView implements IView, LaunchListener {

	private static final String NEW_LINE = System.lineSeparator();

	private String ENTRY = ">";

	static private Map<Console, StringBuilder> mementoHistory = new HashMap<>();
	static private Map<Console, Color> mementoTextColor = new HashMap<>();
	static private Map<Console, Color> mementoBackgroundColor = new HashMap<>();

	private Console console;

	private Composite composite;
	private StyledText text;
	private Composite toolbar;
	private Button btnBackgroundColor;
	private Button btnTextColor;
	private Button btnClear;

	@Override
	public void launched() {
		try {
			ConsoleIService cs = DirmiServer.INSTANCE
			    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
			if (cs != null)
				cs.addListener(viewApi);
		} catch (RemoteException e) {
		}
	}

	private ConsoleViewService viewApi = new ConsoleViewService() {
		@Override
		public void newOutput(final String out) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					text.append(out);
					mementoHistory.get(console).append(out);
					text.setSelection(text.getText().length());
				}
			});
		}

		@Override
		public void clearScreen() throws RemoteException {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					clear();
				}
			});
		}

		@Override
		public void deleteFirstLine() throws RemoteException {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					text.setText(text.getText().substring(text.getLine(0).length() + 1));
					mementoHistory.get(console).delete(0,
					    mementoHistory.get(console).indexOf("\n") + 1);
					text.setSelection(text.getText().length());
				}
			});
		}
	};

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createPartControl(Composite parent, Shell shell) {
		initLayout(parent);
		iniComponents();
		initActions(shell);
		addStyles();
	}

	private void initLayout(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new BorderLayout(0, 0));

		toolbar = new Composite(composite, SWT.NONE);
		toolbar.setLayoutData(BorderLayout.WEST);
		toolbar.setLayout(new GridLayout(1, false));

		btnClear = new Button(toolbar, SWT.BORDER | SWT.FLAT);
		btnTextColor = new Button(toolbar, SWT.BORDER | SWT.FLAT);
		btnBackgroundColor = new Button(toolbar, SWT.BORDER | SWT.FLAT);
	}

	private void iniComponents() {
		text = new StyledText(composite, SWT.WRAP | SWT.V_SCROLL);

		btnClear.setText(" CL ");
		btnTextColor.setText(" TC ");
		btnBackgroundColor.setText(" BC ");
		btnClear.setToolTipText("Clear");
		btnTextColor.setToolTipText("Text color");
		btnBackgroundColor.setToolTipText("Background color");

	}

	private void initActions(final Shell shell) {
		text.addVerifyKeyListener(new VerifyKeyListener() {
			@Override
			public void verifyKey(VerifyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN)
					return;
				if (e.keyCode == SWT.ARROW_LEFT)
					return;
				if (e.keyCode == SWT.ARROW_RIGHT)
					return;
				if (e.keyCode == SWT.ARROW_UP)
					return;
				if (e.keyCode == SWT.CTRL)
					return;
				if (e.keyCode == SWT.SHIFT)
					return;

				if (console.getConsoleType().equals(ConsoleType.IN)) {
					e.doit = false;
					return;
				}
				if (!text.getLine(text.getLineCount() - 1).startsWith(ENTRY)) {
					if (text.getLine(text.getLineCount() - 1).length() != 0) {
						text.append(NEW_LINE);
						mementoHistory.get(console).append(NEW_LINE);
					}
					text.append(ENTRY);
				}
				if (e.keyCode == SWT.BS) {
					if (text.getText().length()
					    - text.getText().lastIndexOf(ENTRY) == 1) {
						e.doit = false;
					}
				}
				text.setSelection(text.getText().length());
			}
		});
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					text.setSelection(text.getText().length());
					String lastLine = text.getLine(text.getLineCount() - 1);
					mementoHistory.get(console).append(lastLine + NEW_LINE);
					String cmd = lastLine.substring(1);
					ConsoleIService api = DirmiServer.INSTANCE
					    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
					if (api != null)
						try {
							api.newCommand(cmd);
						} catch (RemoteException e) {
						}
				}
			}
		});
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clear();
				ConsoleIService api = DirmiServer.INSTANCE
				    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
				if (api != null)
					try {
						api.clearHistory();
					} catch (RemoteException e1) {
					}
			}
		});
		btnTextColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(shell);
				cd.setText("Text Color");
				cd.setRGB(text.getForeground().getRGB());
				RGB newColor = cd.open();
				if (newColor == null)
					return;
				Color color = new Color(shell.getDisplay(), newColor);
				text.setForeground(color);
				mementoTextColor.put(console, color);
				text.setFocus();
			}
		});
		btnBackgroundColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(shell);
				cd.setText("Background Color");
				cd.setRGB(text.getBackground().getRGB());
				RGB newColor = cd.open();
				if (newColor == null)
					return;
				Color color = new Color(shell.getDisplay(), newColor);
				text.setBackground(color);
				mementoBackgroundColor.put(console, color);
				text.setFocus();
			}
		});
	}

	private void clear() {
		text.setText("");
		text.setSelection(text.getText().length());
		mementoHistory.get(console).setLength(0);
		text.setFocus();
	}

	private void addStyles() {
		composite.setBackground(
		    Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		toolbar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
	}

	public void init(EObject bo, TransactionalEditingDomain ed) {
		this.console = (Console) bo;

		RuminaqLaunchDelegate.addLaunchListener(this);
		try {
			ConsoleIService cs = DirmiServer.INSTANCE
			    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
			if (cs != null)
				cs.addListener(viewApi);
		} catch (RemoteException e) {
		}

		/* History */
		StringBuilder history = mementoHistory.get(bo);
		if (history == null) {
			history = new StringBuilder();
			ConsoleIService api = DirmiServer.INSTANCE
			    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
			if (api != null)
				try {
					history.append(api.getHistory());
				} catch (RemoteException e) {
				}
			mementoHistory.put(console, history);
		}
		text.setText(history.toString());
		text.setSelection(text.getText().length());

		/* Colors */
		Color textColor = mementoTextColor.get(bo);
		if (textColor == null) {
			textColor = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
			mementoTextColor.put(console, textColor);
		}
		text.setForeground(textColor);

		Color bgColor = mementoBackgroundColor.get(bo);
		if (bgColor == null) {
			bgColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			mementoBackgroundColor.put(console, bgColor);
		}
		text.setBackground(bgColor);

		updateType();
	}

	@Override
	public void setFocus() {
		text.setFocus();
		text.setSelection(text.getText().length());
		if (console == null)
			return;
		updateType();
	}

	@Override
	public void dispose() {
		RuminaqLaunchDelegate.removeLaunchListener(ConsoleView.this);
		try {
			ConsoleIService cs = DirmiServer.INSTANCE
			    .getRemote(Util.getUniqueId(console), ConsoleIService.class);
			if (cs != null) {
				cs.removeListener(viewApi);
			}
		} catch (RemoteException e) {
		}
	}

	public void updateType() {
		/* Prompt */
		if (console.getConsoleType().equals(ConsoleType.IN))
			ENTRY = "";
		else
			ENTRY = ">";
	}
}
