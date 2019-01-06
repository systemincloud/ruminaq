/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse.wizards.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.ruminaq.util.EclipseUtil;

/**
 * Creates directories for eclipse sources.
 *
 * @author Marek Jagielski
 */
public final class SourceFolders {

    public static final String MAIN_RESOURCES = "src/main/resources";
    public static final String TEST_RESOURCES = "src/test/resources";
    public static final String TASK_FOLDER = "tasks";
    public static final String DIAGRAM_FOLDER = MAIN_RESOURCES + "/" + TASK_FOLDER + "/";
    public static final String TEST_DIAGRAM_FOLDER = TEST_RESOURCES + "/" + TASK_FOLDER + "/";


    private SourceFolders() {
    }

    /**
     * Creates directories for eclipse sources.
     *
     * @param project Eclipse IProject reference
     */
    static void createSourceFolders(IProject project) throws CoreException {
        EclipseUtil.createFolderWithParents(project, MAIN_RESOURCES);
        EclipseUtil.createFolderWithParents(project, DIAGRAM_FOLDER);
        EclipseUtil.createFolderWithParents(project, TEST_RESOURCES);
        EclipseUtil.createFolderWithParents(project, TEST_DIAGRAM_FOLDER);
    }
}
