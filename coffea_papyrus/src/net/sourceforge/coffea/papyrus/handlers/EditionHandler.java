package net.sourceforge.coffea.papyrus.handlers;


import net.sourceforge.coffea.editors.handlers.ReverseHandler;
import net.sourceforge.coffea.papyrus.JavaElementsEditionReceiver;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Simple edition using the UML editor : : uses the workspace 
 * {@link org.eclipse.jdt.core.IJavaElement <em>Java</em> elements} to produce 
 * the model
 * @see org.eclipse.jdt.core.IJavaElement
 * @see ReverseHandler
 */
public class EditionHandler extends ReverseHandler {
	
	/** Receiver handling the edition interactions */
	private static JavaElementsEditionReceiver editionReceiver;
	
	/**
	 * Returns the receiver handling the edition interactions
	 * @return Receiver handling the edition interactions
	 */
	public static synchronized JavaElementsEditionReceiver 
	getEditionReceiver() {
		if(editionReceiver==null) {
			editionReceiver = 
				new JavaElementsEditionReceiver();
		}
		return editionReceiver;
	}

	@Override
	public synchronized Object execute(ExecutionEvent event) 
	throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return getEditionReceiver().editSelectedJavaElements(
				workbenchWindow
		);
	}
	
}
