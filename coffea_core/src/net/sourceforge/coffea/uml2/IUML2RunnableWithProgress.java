package net.sourceforge.coffea.uml2;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

/** Runnable with progress aiming to remove a dependence from the Eclipse UI */
public interface IUML2RunnableWithProgress {
	
	/** 
	 * Running with a progress monitor
	 * @param monitor
	 * Progress monitor
	 */
	public void run(IProgressMonitor monitor) 
	throws InvocationTargetException, InterruptedException;

}
