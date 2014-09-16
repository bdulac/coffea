package net.sourceforge.coffea.papyrus.editors.policies;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.uml.NamedElement;

/** 
 * {@link UMLClassDiagramJavaEditor Class diagram} edit policy, little 
 * trick : it is a simple copy from 
 * {@code org.eclipse.uml2.diagram.clazz.edit.policies.OpenDiagramEditPolicy}
 * to shortcut an access restriction
 */
public class OpenUMLClassDiagramJavaEditPolicy extends OpenEditPolicy {

	// Override
	protected Command getOpenCommand(Request request) {
		EditPart targetEditPart = getTargetEditPart(request);
		if (false == targetEditPart.getModel() instanceof View) {
			return null;
		}
		View view = (View) targetEditPart.getModel();
		Style link = 
				view.getStyle(NotationPackage.eINSTANCE.getHintedDiagramLinkStyle());
		if (false == link instanceof HintedDiagramLinkStyle) {
			return null;
		}
		// return new ICommandProxy(new OpenDiagramCommand((HintedDiagramLinkStyle) link));
		HintedDiagramLinkStyle linkStyle = (HintedDiagramLinkStyle)link;
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(linkStyle);
		// TODO package edition !
		// return new ICommandProxy(new OpenClassDiagramCommand(domain, evt, ctx, linkStyle));
		return null;
	}

	/*
	protected static class OpenDiagramCommand extends
	AbstractTransactionalCommand {

		private final HintedDiagramLinkStyle diagramFacet;

		OpenDiagramCommand(HintedDiagramLinkStyle linkStyle) {
			// Editing domain is taken for original diagram,
			// if we open diagram from another file, we should use another
			// editing domain
			super(
					TransactionUtil.getEditingDomain(linkStyle),
					Messages.CommandName_OpenDiagram, 
					null
			);
			diagramFacet = linkStyle;
		}

		protected CommandResult doExecuteWithResult(
				IProgressMonitor monitor,
				IAdaptable info
		) throws ExecutionException {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			boolean opened = false;
			if(win!=null) {
				IWorkbenchPage[] pages = win.getPages();
				if(pages!=null) {
					opened = openFromPages(pages);
				}
				if(!opened) {
					EditionHandler.getEditionReceiver()
					.reverseFromSelectedJavaElement(
							win, 
							ReverseHandler.getInteractionsReceiver()
							.getLastSourceViewId()
					);
				}
			}
			return CommandResult.newOKCommandResult();
		}



		protected Diagram getDiagramToOpen() {
			return diagramFacet.getDiagramLink();
		}

		protected Diagram intializeNewDiagram() throws ExecutionException {
			Diagram d = 
					ViewService.createDiagram(
							getDiagramDomainElement(),
							getDiagramKind(), 
							getPreferencesHint()
					);
			if (d == null) {
				throw new ExecutionException(
						"Can't create diagram of '"+ getDiagramKind() 
						+ "' kind"
				);
			}
			diagramFacet.setDiagramLink(d);
			assert diagramFacet.eResource() != null;
			diagramFacet.eResource().getContents().add(d);
			try {
				new WorkspaceModifyOperation() {

					protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
						try {
							for (
									Iterator<Resource> it = 
										diagramFacet.eResource()
										.getResourceSet().getResources()
										.iterator(); 
									it.hasNext();
							) {
								Resource nextResource = it.next();
								if (
										nextResource.isLoaded()
										&& !getEditingDomain().isReadOnly(nextResource)
								) {
									nextResource.save(UMLDiagramEditorUtil.getSaveOptions());
								}
							}
						} catch (IOException ex) {
							throw new InvocationTargetException(
									ex,
									"Save operation failed"
							);
						}
					}
				}.run(null);
			} catch (InvocationTargetException e) {
				throw new ExecutionException(
						"Can't create diagram of '"
						+ getDiagramKind() 
						+ "' kind", 
						e
				);
			} catch (InterruptedException e) {
				throw new ExecutionException(
						"Can't create diagram of '"
						+ getDiagramKind() 
						+ "' kind", 
						e
				);
			}
			return d;
		}

		protected EObject getDiagramDomainElement() {
			// use same element as associated with EP
			return ((View) diagramFacet.eContainer()).getElement();
		}
	}
	*/

	protected static String getDiagramName(EObject diagramDomainElement) {
		String result = null;
		if (diagramDomainElement instanceof NamedElement) {
			NamedElement named = (NamedElement) diagramDomainElement;
			result = named.getQualifiedName();
			if (result == null || result.length() == 0) {
				result = named.getName();
			}
		}
		return result;
	}

}