/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse;

import java.net.URI;

import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants;
import org.ruminaq.consts.Constants.SicPlugin;

/**
 *
 * @author Marek Jagielski
 */
public class Activator extends AbstractUIPlugin {

    private static Activator plugin;

    private static ImageCache imageCache;

    @Override
    public void start(BundleContext context) throws Exception {
      plugin = this;
        super.start(context);
        imageCache = new ImageCache();
        IPathVariableManager pathVariableManager = ResourcesPlugin.getWorkspace().getPathVariableManager();
//        pathVariableManager.setURIValue(Constants.M2_HOME, new URI(MavenPlugin.getMaven().getLocalRepository().getUrl()));
//    RuminaqResourceChange.getInstance();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // Image cache destroy itself so image references are cleaned up
        imageCache.dispose();
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    public static Version getVersion() {
        return getDefault().getBundle().getVersion();
    }

    public static String getVersionString() {
        return getVersion().toString().replace(Constants.QUALIFIER, Constants.SNAPSHOT);
    }

    public static String getBaseVersionString() {
        return getVersion().getMajor() + "." + getVersion().getMinor() + "." + getVersion().getMicro();
    }

    public static Shell getShell() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    public static IWorkbenchPage getActivePage()        {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (workbenchWindow != null) return workbenchWindow.getActivePage();
        return null;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(SicPlugin.ECLIPSE_ID.s(), path);
    }

    /**
     * Gets an image from this plugin and serves it from the {@link ImageCache}.
     */
    public static Image getImage(PluginImage pluginImage) {
        return imageCache.getImage(pluginImage);
    }

    /**
     * Gets an image from this plugin and serves it from the {@link ImageCache}.
     * @return an Image if the image was found, null otherwise
     */
    public static Image getImage(ImageDescriptor imageDescriptor) {
        return ImageCache.getImage(imageDescriptor);
    }

    /**
     * Gets an image descriptor for an image from this plugin.
     * @return an ImageDescriptor if the image was found, null otherwise
     */
    public static ImageDescriptor getImageDescriptor(PluginImage pluginImage) {
        return getImageDescriptor(pluginImage.getImagePath());
    }

}
