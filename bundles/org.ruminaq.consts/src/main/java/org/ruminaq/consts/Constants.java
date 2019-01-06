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
package org.ruminaq.consts;

public final class Constants {

    public enum SicPlugin {
        CONSTS_ID("org.ruminaq.consts"),
        ECLIPSE_ID("org.ruminaq.eclipse"),
        GUI_ID("org.ruminaq.gui"),
        LAUNCH_ID("org.ruminaq.launch"),
        MODEL_ID("org.ruminaq.model"),
        LOGS_ID("org.ruminaq.logs"),
        RUNNER_ID("org.ruminaq.runner"),
        UTIL_ID("org.ruminaq.util"),
        TASKS_ID("org.ruminaq.tasks"),
        PREFS_ID("org.ruminaq.prefs"),
        ;
        private String symbolicName;

        SicPlugin(String symbolicName) {
        	this.symbolicName = symbolicName;
        }

        public String s() {
        	return this.symbolicName;
        }
    }

    public static final String ECLIPSE_CORE_RUNTIME_PLUGIN_ID = "org.eclipse.core.runtime";
    public static final String ECLIPSE_CORE_RESOURCES_PLUGIN_ID = "org.eclipse.core.resources";
    public static final String ECLIPSE_CORE_JOBS_PLUGIN_ID = "org.eclipse.core.jobs";
    public static final String ECLIPSE_EQUINOX_COMMON_PLUGIN_ID = "org.eclipse.equinox.common";
    public static final String ECLIPSE_OSGI_PLUGIN_ID = "org.eclipse.osgi";

    public static final String EMF_ECORE_PLUGIN_ID       = "org.eclipse.emf.ecore";       //$NON-NLS-1$
    public static final String EMF_COMMON_PLUGIN_ID      = "org.eclipse.emf.common";      //$NON-NLS-1$
    public static final String EMF_TRANSACTION_PLUGIN_ID = "org.eclipse.emf.transaction"; //$NON-NLS-1$
    public static final String EMF_XMI_PLUGIN_ID         = "org.eclipse.emf.ecore.xmi";   //$NON-NLS-1$
    public static final String GRAPHITI_MM_PLUGIN_ID     = "org.eclipse.graphiti.mm";     //$NON-NLS-1$

    public static final String VALIDATION_MARKER = "org.ruminaq.validation.marker"; //$NON-NLS-1$

    public static final String MAIN_JAVA = "src/main/java";                               //$NON-NLS-1$
    public static final String TEST_JAVA = "src/test/java";                               //$NON-NLS-1$
    public static final String MAIN_PYTHON          = "src/main/py";                                 //$NON-NLS-1$
    public static final String TEST_PYTHON          = "src/test/py";                                 //$NON-NLS-1$
    public static final String MAIN_R               = "src/main/r";                                  //$NON-NLS-1$
    public static final String TEST_R               = "src/test/r";                                  //$NON-NLS-1$
    public static final String OUTPUT_CLASSES       = "target/classes";                              //$NON-NLS-1$
    public static final String MAVEN_CONTAINER_ID   = "org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER";  //$NON-NLS-1$
    public static final String EXTERNALTOOLBUILDERS = ".externalToolBuilders";                       //$NON-NLS-1$

    public static final String EXTENSION                   = "rumi";               //$NON-NLS-1$
    public static final String DIAGRAM_EXTENSION_DOT       = "." + EXTENSION;      //$NON-NLS-1$

    public static final String QUALIFIER = ".qualifier"; //$NON-NLS-1$
    public static final String SNAPSHOT  = "-SNAPSHOT";  //$NON-NLS-1$

    public static final String DIAGRAM_EDITOR_ID      = "org.ruminaq.eclipse.editor.ruminaqEditor";     //$NON-NLS-1$
    public static final String TEST_DIAGRAM_EDITOR_ID = "org.ruminaq.eclipse.editor.ruminaqEditorTest"; //$NON-NLS-1$

    public static final String NATURE_ID = "org.ruminaq.eclipse.nature.RuminaqProjectNature"; //$NON-NLS-1$
    public static final String MAVEN_NATURE_ID = "org.eclipse.m2e.core.maven2Nature";                         //$NON-NLS-1$

    public static final String LABEL_PROPERTY      = "label";                       //$NON-NLS-1$
    public static final String INTERNAL_PORT       = "internal-port";               //$NON-NLS-1$
    public static final String PORT_LABEL_PROPERTY = "port-label";                  //$NON-NLS-1$
    public static final String SIMPLE_CONNECTION_POINT = "SIMPLE_CONNECTION_POINT"; //$NON-NLS-1$

    public static final int CONTEXT_BUTTON_UPDATE = 1 << 1;
    public static final int CONTEXT_BUTTON_REMOVE = 1 << 2;
    public static final int CONTEXT_BUTTON_DELETE = 1 << 3;

    public static final String CAN_DELETE = "can-delete"; //$NON-NLS-1$

    public static final String INF = "inf"; //$NON-NLS-1$
    public static final String M2_HOME = "M2_HOME"; //$NON-NLS-1$

    private Constants() { }

}
