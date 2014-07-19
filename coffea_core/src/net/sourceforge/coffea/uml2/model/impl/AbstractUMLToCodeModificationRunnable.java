package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.uml2.uml.Element;

import net.sourceforge.coffea.uml2.model.IElementService;

/** 
 * Abstract classification of a code modification runnable with progress 
 * operating from an UML modeled objective
 * @param <U>
 * Type of the UML element to take as objective  
 * @param <R>
 * Type of the handler to use for the modification result
 */
public abstract class AbstractUMLToCodeModificationRunnable
<U extends Element, R extends IElementService> 
implements IUMLToCodeModificationRunnable<U, R> {

	/** Element modeling the objective to aim, the result to get */
	protected U objective;
	
	/** Result got from the modification */
	protected R result;
	
	public void setObjective(U o) {
		objective = o;
	}
	
	public U getObjective() {
		return objective;
	}
	
	public abstract void run(
			IProgressMonitor monitor
	) throws InvocationTargetException,
    InterruptedException;

	public R getResult() {
		return result;
	}	

}