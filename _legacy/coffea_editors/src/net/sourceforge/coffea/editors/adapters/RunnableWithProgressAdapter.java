package net.sourceforge.coffea.editors.adapters;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/** Adapter of a runnable with progress for the Eclipse UI */
public class RunnableWithProgressAdapter implements IRunnableWithProgress {
	
	protected IUML2RunnableWithProgress adaptedRunnable;

	public void run(IProgressMonitor monitor) 
	throws InvocationTargetException, InterruptedException {
		adaptedRunnable.run(monitor);
	}

}
