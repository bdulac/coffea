package net.sourceforge.coffea.java.handlers;

import net.sourceforge.coffea.java.JavaElementsReverseReceiver;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Simple reverse to an UML model : uses the workspace 
 * {@link org.eclipse.jdt.core.IJavaElement <em>Java</em> elements} to produce 
 * the model
 * @see org.eclipse.jdt.core.IJavaElement
 * @see EditionHandler
 */
public class ReverseHandler extends AbstractHandler {
	
	/** Receiver handling the interactions */
	private static JavaElementsReverseReceiver interactionsReceiver;
	
	/**
	 * Returns the receiver handling the interactions
	 * @return Receiver handling the interactions
	 */
	public static synchronized JavaElementsReverseReceiver 
	getInteractionsReceiver() {
		if(interactionsReceiver==null) {
			interactionsReceiver = 
				new JavaElementsReverseReceiver();
		}
		return interactionsReceiver;
	}
	
	/** Simple reverse handler construction */
	public ReverseHandler() {
		super();
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return getInteractionsReceiver().reverseFromJavaElements(
				workbenchWindow
		);
	}
}