package net.sourceforge.coffea.papyrus.handlers;

import net.sourceforge.coffea.java.handlers.ASTServiceHandler;
import net.sourceforge.coffea.java.handlers.JavaModelServiceHandler;
import net.sourceforge.coffea.papyrus.ASTPapyrusServiceLocator;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for an UML model service in coordination with Papyrus editors : uses an AST to 
 * produce the service
 * @see AST
 * @see JavaModelServiceHandler
 */
public class PapyrusASTServiceHandler extends ASTServiceHandler {
	
	/** Receiver handling the edition interactions */
	private static ASTPapyrusServiceLocator editionReceiver;
	
	/**
	 * Returns the receiver handling the edition interactions
	 * @return Receiver handling the edition interactions
	 */
	public static synchronized ASTPapyrusServiceLocator getServiceLocator() {
		if(editionReceiver==null) {
			editionReceiver = 
				new ASTPapyrusServiceLocator();
		}
		return editionReceiver;
	}
	
	/** Full edition handler construction */
	public PapyrusASTServiceHandler() {
		super();
	}

	@Override
	public IModelService execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return getServiceLocator().getModelService(workbenchWindow);
	}
}