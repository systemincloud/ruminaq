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
package org.ruminaq.eclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ruminaq.validation.ProjectValidator;

/**
 *
 * @author Marek Jagielski
 */
public class RuminaqBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.ruminaq.eclipse.ruminaqBuilder";

	class RuminaqDeltaVisitor implements IResourceDeltaVisitor {
		IProgressMonitor monitor;

		public RuminaqDeltaVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
        public boolean visit(IResourceDelta delta) throws CoreException {
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				validate(delta, monitor);
				break;
			case IResourceDelta.REMOVED:
				break;
			case IResourceDelta.CHANGED:
				validate(delta, monitor);
				break;
			}
			return true;
		}
	}

	class RuminaqResourceVisitor implements IResourceVisitor {
		IProgressMonitor monitor;

		public RuminaqResourceVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
        public boolean visit(IResource resource) {
			validate(resource, monitor);
			return true;
		}
	}

	@Override
    @SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) fullBuild(monitor);
		else {
			IResourceDelta delta = getDelta(getProject());
			if(delta == null) fullBuild(monitor);
			else incrementalBuild(delta, monitor);
		}
		return null;
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		delta.accept(new RuminaqDeltaVisitor(monitor));
	}

	void validate(IResourceDelta delta, IProgressMonitor monitor) {
		ProjectValidator.validate(delta, monitor);
	}

	void validate(IResource resource, IProgressMonitor monitor) {
		ProjectValidator.validate(resource, monitor);
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new RuminaqResourceVisitor(monitor));
		} catch (CoreException e) { }
	}

}
