package net.sourceforge.coffea.uml2tools.handlers;

import net.sourceforge.coffea.editors.handlers.FullReverseHandler;
import net.sourceforge.coffea.uml2tools.ASTNodesEditionReceiver;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Complete edition using the UML editor : parses code producing an AST
 * @see org.eclipse.core.commands.IHandler
 * @see FullReverseHandler
 */
public class FullEditionHandler extends FullReverseHandler {
	
	/** Receiver handling the edition interactions */
	private static ASTNodesEditionReceiver editionReceiver;
	
	/**
	 * Returns the receiver handling the edition interactions
	 * @return Receiver handling the edition interactions
	 */
	public static synchronized ASTNodesEditionReceiver getEditionReceiver() {
		if(editionReceiver==null) {
			editionReceiver = 
				new ASTNodesEditionReceiver();
		}
		return editionReceiver;
	}
	
	/** Full edition handler construction */
	public FullEditionHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return getEditionReceiver().editFromASTNodes(
				workbenchWindow
		);
	}
}