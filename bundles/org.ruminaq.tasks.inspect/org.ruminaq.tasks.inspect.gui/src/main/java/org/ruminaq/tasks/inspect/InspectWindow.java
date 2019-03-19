package org.ruminaq.tasks.inspect;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.swt.widgets.Display;
import org.ruminaq.launch.LaunchListener;
import org.ruminaq.launch.RuminaqLaunchDelegate;
import org.ruminaq.runner.dirmi.DirmiServer;
import org.ruminaq.tasks.SicWindow;
import org.ruminaq.tasks.Windows;
import org.ruminaq.tasks.inspect.impl.InspectIService;
import org.ruminaq.tasks.inspect.impl.InspectWindowService;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;
import org.ruminaq.runner.util.Util;

public class InspectWindow extends JFrame implements LaunchListener, SicWindow {

	private static final long serialVersionUID = 1L;

	private JScrollPane root;
	private JTextArea   text;
	private Inspect     inspect;
	private String      displayedValue = "";

	private InspectWindowService windowApi = new InspectWindowService(){
		@Override
		public void newValue(final String value) throws RemoteException {
			Display.getDefault().syncExec(new Runnable() {
			    public void run() {
			    	displayedValue = value;
			    	text.setText(displayedValue);
			    }});
		}};

	public void init(Object o) {
		this.inspect = (Inspect) o;

		RuminaqLaunchDelegate.addLaunchListener(this);
		addWindowServiceListener();
		setTitle(inspect.getId());

		InspectIService api = DirmiServer.INSTANCE.getRemote(Util.getUniqueId(inspect), InspectIService.class);
		if(api != null)	try {
            String lastValue = api.getLastValue();
            displayedValue = lastValue;
	    } catch (RemoteException e) { }

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try { InspectWindow.this.setVisible(true);
				} catch (Exception e) {	}
			}});
	}

	public InspectWindow() {
		initLayout();
		initComponents();
		initActions();

		pack();
	}

	private void initLayout() {
		setLocationRelativeTo(null);

		text = new JTextArea(displayedValue);
		root = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		root.setPreferredSize(new Dimension(200, 30));
		setContentPane(root);
	}

	private void initComponents() {
		text.setBorder(null);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	private void initActions() {
		root.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {
			}});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				Windows.INSTANCE.disconnectWindow(inspect);
				RuminaqLaunchDelegate.removeLaunchListener(InspectWindow.this);
				try {
					InspectIService cs = DirmiServer.INSTANCE.getRemote(Util.getUniqueId(inspect), InspectIService.class);
					if(cs != null) cs.removeListener(windowApi);
				} catch (RemoteException ex) { }
			}});
	}

	@Override public void dirmiStarted() { }
	@Override public void launched()     { addWindowServiceListener(); }
	@Override public void stopped()      { }

	public void addWindowServiceListener() {
		try {
			InspectIService cs = DirmiServer.INSTANCE.getRemote(Util.getUniqueId(inspect), InspectIService.class);
			if(cs != null) cs.addListener(windowApi);
		} catch (RemoteException e) { }
	}
}
