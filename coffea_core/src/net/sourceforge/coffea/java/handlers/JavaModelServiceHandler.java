package net.sourceforge.coffea.java.handlers;

import net.sourceforge.coffea.java.JavaModelServiceLocator;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for an UML model service : uses the Java Model to produce the service
 * @see IJavaModel
 * @see ASTServiceHandler
 */
public class JavaModelServiceHandler extends AbstractHandler {
	
	/** Receiver handling the interactions */
	private static JavaModelServiceLocator interactionsReceiver;
	
	/**
	 * Returns the service locator
	 * @return Service locator
	 */
	public static synchronized JavaModelServiceLocator getServiceLocator() {
		if(interactionsReceiver==null) {
			interactionsReceiver = 
				new JavaModelServiceLocator();
		}
		return interactionsReceiver;
	}
	
	/** Simple reverse handler construction */
	public JavaModelServiceHandler() {
		super();
	}
	
	// @Override
	public IModelService execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IModelService modelSrv = 
				getServiceLocator().getModelService(workbenchWindow);
		modelSrv.dispose();
		return modelSrv;
	}
}