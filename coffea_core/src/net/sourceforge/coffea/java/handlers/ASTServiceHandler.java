package net.sourceforge.coffea.java.handlers;

import net.sourceforge.coffea.java.ASTServiceLocator;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for an UML model service : uses an AST to produce the service
 * @see AST
 * @see JavaModelServiceHandler
 */
public class ASTServiceHandler extends AbstractHandler {

	/** Service locator */
	protected ASTServiceLocator serviceLocator;
	
	/** Full reverse handler construction */
	public ASTServiceHandler() {
		super();
		serviceLocator = new ASTServiceLocator();
	}
	
	public IModelService execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return serviceLocator.getModelService(workbenchWindow);
	}
}