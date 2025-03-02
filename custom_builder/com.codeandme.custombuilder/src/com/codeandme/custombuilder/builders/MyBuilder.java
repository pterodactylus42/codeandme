/*******************************************************************************
 * Copyright (c) 2015 Christian Pontesegger
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Pontesegger - initial implementation
 *******************************************************************************/
package com.codeandme.custombuilder.builders;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MyBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "com.codeandme.custombuilder.myBuilder";

	@Override
	protected IProject[] build(final int kind, final Map<String, String> args, final IProgressMonitor monitor)
			throws CoreException {

		printToConsole("MyBuilder.build triggered");

		getProject();

		switch (kind) {

		case FULL_BUILD:
			break;

		case INCREMENTAL_BUILD:
			break;

		case AUTO_BUILD:
			break;
		}

		return null;
	}
	
	class MyDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			printToConsole("MyDeltaVisitor.visit triggered");
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				process(resource);
				break;
			case IResourceDelta.REMOVED:
				//
				break;
			case IResourceDelta.CHANGED:
				process(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class MyResourceVisitor implements IResourceVisitor {
		@Override
		public boolean visit(IResource resource) {
			printToConsole("MyResourceVisitor.visit triggered");
			process(resource);
			// return true to continue visiting children.
			return true;
		}
	}
	
	private void process(IResource resource) {
		printToConsole("process resource" + resource.getName());
		
	}
	
	private void printToConsole(final String message)
	{
		final MessageConsole console = getConsole();
		final MessageConsoleStream messageStream = console.newMessageStream();

		messageStream.println(message);
		show(console);
		
		try {
			messageStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MessageConsole getConsole()
	{
		final String name = "MyBuilder";

		  ConsolePlugin plugin = ConsolePlugin.getDefault();
		  IConsoleManager conMan = plugin.getConsoleManager();
		  IConsole[] existing = conMan.getConsoles();
		  for (int i = 0; i < existing.length; i++)
		     if (name.equals(existing[i].getName()))
		        return (MessageConsole) existing[i];

		  MessageConsole myConsole = new MessageConsole(name, null);
		  conMan.addConsoles(new IConsole[]{myConsole});
		  return myConsole;
	}

	private void show(final MessageConsole console) {
	  Display.getDefault().asyncExec(new Runnable() {
		    @Override
		    public void run() {
		        try {
					IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = win.getActivePage();
					String id = IConsoleConstants.ID_CONSOLE_VIEW;
					IConsoleView view = (IConsoleView) page.showView(id);
					  view.display(console);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		});
	}


}
