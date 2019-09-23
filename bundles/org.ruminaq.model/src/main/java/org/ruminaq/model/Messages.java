package org.ruminaq.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.ruminaq.model.messages";

  public static String UI_InputPort_long_description;
  public static String UI_OutputPort_long_description;

  public static String UI_JavaTask_long_description;
  public static String UI_ScriptTask_long_description;
  public static String UI_EmbeddedTask_long_description;

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
