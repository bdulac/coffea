package net.sourceforge.coffea.editors.handlers;

import net.sourceforge.coffea.editors.ASTNodesReverseReceiver;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Full reverse to an UML model : parses code building an 
 * {@link org.eclipse.jdt.core.dom.AST AST} to produce the model
 * @see org.eclipse.jdt.core.dom.AST
 * @see ReverseHandler
 */
public class FullReverseHandler extends AbstractHandler {

	/** Receiver handling the interactions */
	protected ASTNodesReverseReceiver interactionsReceiver;
	
	/** Full reverse handler construction */
	public FullReverseHandler() {
		super();
		interactionsReceiver = new ASTNodesReverseReceiver();
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return interactionsReceiver.reverseFromASTNodes(workbenchWindow);
	}
}