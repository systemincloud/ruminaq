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
package org.ruminaq.validation.constraints;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.EclipseUtil;

public class LoopedEmbeddedTaskConstraint extends AbstractModelConstraint {

    @Override
    public IStatus validate(IValidationContext ctx) {
        EObject eObj = ctx.getTarget();
        if (ctx.getEventType() == EMFEventType.NULL && eObj instanceof EmbeddedTask)
            return validate(ctx, (EmbeddedTask) eObj);
        return ctx.createSuccessStatus();
    }

    private IStatus validate(IValidationContext ctx, EmbeddedTask task) {
        String path = task.getImplementationTask();
        if(path == null || path.equals("")) return ctx.createSuccessStatus();
        URI modelPath = EclipseUtil.getModelPathFromEObject(task);
        String prefix = "/" + modelPath.segment(0) + "/";
        MainTask embeddedTask = loadTask(modelPath);

        List<String> deph = new ArrayList<>();
        deph.add(EclipseUtil.removeFristSegments(modelPath, 1).toString());
        boolean loop = detectLoop(prefix, embeddedTask, deph);

        if(loop)return ctx.createFailureStatus();
        else return ctx.createSuccessStatus();
    }

    private boolean detectLoop(String prefix, MainTask mainTask, List<String> deph) {
        for(Task t : mainTask.getTask()) {
            if(t instanceof EmbeddedTask) {
                String path = ((EmbeddedTask)t).getImplementationTask();
                if(deph.contains(path)) return true;
                else {
                    MainTask embeddedTask = loadTask(URI.createURI(prefix + path));
                    if(embeddedTask == null) continue;
                    deph.add(path);
                    boolean loop = detectLoop(prefix, embeddedTask, deph);
                    deph.remove(deph.size() - 1);
                    if(loop) return true;
                }
            }
        }
        return false;
    }

    private MainTask loadTask(URI uri) {
        MainTask mt = null;
        ResourceSet resSet = new ResourceSetImpl();
        Resource resource = null;
        try {
            resource = resSet.getResource(uri, true);
        } catch(Exception e) { }
        if(resource == null) return null;

        if(resource.getContents().size() > 0) mt = (MainTask) resource.getContents().get(1);
        return mt;
    }

}
