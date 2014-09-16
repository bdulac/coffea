package net.sourceforge.coffea.papyrus.handlers;

import net.sourceforge.coffea.java.handlers.ReverseHandler;
import net.sourceforge.coffea.papyrus.JavaElementsEditionReceiver;
import net.sourceforge.coffea.papyrus.commands.OpenClassDiagramCommand;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForHandlers;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.uml2.uml.Model;

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
	public synchronized IModelService execute(ExecutionEvent event) 
	throws ExecutionException {
		IWorkbenchWindow workbenchWindow = 
			HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IModelService service = getEditionReceiver().editSelectedJavaElements(
				workbenchWindow
		);
		Model m = service.getUMLElement();
		IOperationHistory history = OperationHistoryFactory.getOperationHistory();
		try {
			// EditPart targetEditPart = getTargetEditPart(request);

			IStructuredSelection selection = (IStructuredSelection)PlatformUI
					.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService().getSelection();
					
			if(selection != null) {
				// EObject selectedEObject = EMFHelper.getEObject(selection.getFirstElement());
				/* TODO Pour Command ?
				TransactionalEditingDomain domain = 
						TransactionUtil.getEditingDomain(m);
						*/
				ServiceUtilsForHandlers handlerUtils = ServiceUtilsForHandlers.getInstance();
				TransactionalEditingDomain domain = null;
				try {
					// TODO L'event est pas bon : il n'a rien à voir avec Papyrus
					// Hors le handlerUtils a besoin d'un événement qui connaît Papyrus
					// --> Ouverture du doc ? Sélection dans la vue Papyrus ?
					Object ctx = event.getApplicationContext();
					domain = handlerUtils.getTransactionalEditingDomain(event);
					if(domain == null)throw new NullPointerException("Null editing domain");
				} catch (ServiceException e) {
					throw new IllegalStateException(e);
				}
				NotationFactory factory = NotationFactory.eINSTANCE;
				HintedDiagramLinkStyle linkStyle = factory.createHintedDiagramLinkStyle();
				AbstractEMFOperation command = 
						new OpenClassDiagramCommand(domain, event, m, linkStyle);
				history.execute(command, new NullProgressMonitor(), null);
			}
			// return new ICommandProxy(new OpenDiagramCommand((HintedDiagramLinkStyle) link));
			
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return service;
	}
	
}
