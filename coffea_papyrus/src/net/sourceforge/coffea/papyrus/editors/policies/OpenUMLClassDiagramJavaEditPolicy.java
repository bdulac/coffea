package net.sourceforge.coffea.papyrus.editors.policies;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.coffea.editors.handlers.ReverseHandler;
import net.sourceforge.coffea.papyrus.editors.UMLClassDiagramJavaEditor;
import net.sourceforge.coffea.papyrus.handlers.EditionHandler;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IPackageService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.gmf.runtime.emf.commands.core.command
.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.clazz.edit.parts.PackageEditPart;
import org.eclipse.papyrus.uml.diagram.clazz.part.Messages;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditor;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditorUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.uml2.uml.NamedElement;

/** 
 * {@link UMLClassDiagramJavaEditor Class diagram} edit policy, little 
 * trick : it is a simple copy from 
 * {@code org.eclipse.uml2.diagram.clazz.edit.policies.OpenDiagramEditPolicy}
 * to shortcut an access restriction
 */
public class OpenUMLClassDiagramJavaEditPolicy extends OpenEditPolicy {

	protected Command getOpenCommand(Request request) {
		EditPart targetEditPart = getTargetEditPart(request);
		if (false == targetEditPart.getModel() instanceof View) {
			return null;
		}
		View view = (View) targetEditPart.getModel();
		Style link = view.getStyle(NotationPackage.eINSTANCE
				.getHintedDiagramLinkStyle());
		if (false == link instanceof HintedDiagramLinkStyle) {
			return null;
		}
		return new ICommandProxy(new OpenDiagramCommand(
				(HintedDiagramLinkStyle) link));
	}

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
			/*
			if(wb!=null) {
				IEditorRegistry edRegistry = wb.getEditorRegistry();
				if(edRegistry!=null) {
					wb.getA
				}
			}
			 */
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

		protected boolean openFromPages(IWorkbenchPage[] pages) {
			boolean opened = false;
			if(pages!=null) {
				IWorkbenchPage page = null;
				searchPages :
					for(int i=0 ; i<pages.length ; i++) {
						page = pages[i];
						IWorkbenchWindow win = null;
						if(page!=null) {
							win = page.getWorkbenchWindow();
							IEditorReference[] references = 
								page.findEditors(
										null, 
										UMLClassDiagramJavaEditor.ID, 
										IWorkbenchPage.MATCH_ID
								);
							if(references!=null) {
								IEditorReference reference = null;
								IEditorPart editor = null;
								for(int j=0 ; j<references.length ; j++) {
									reference = references[j];
									if(reference!=null) {
										editor = 
											reference.getEditor(false);
										if(
												editor 
												instanceof 
												UMLClassDiagramJavaEditor
										) {
											UMLClassDiagramJavaEditor umlEditor 
											= ((UMLClassDiagramJavaEditor)
														editor);
											IElementService elH = 
												umlEditor
												.getLastSelectedElementHandler();
											if(
													(elH!=null)
													&&(
															elH 
															instanceof 
															IPackageService

													)
											){
												IPackageService packH = 
													(IPackageService)elH;
												if(
														packH.getJavaElement()
														!=null
												) {
													String lastId = 
														ReverseHandler
														.getInteractionsReceiver()
														.getLastSourceViewId();
													opened = true;
													EditionHandler
													.getEditionReceiver()
													.edit(
															packH
															.getJavaElement(), 
															win, 
															lastId
													);
													break searchPages;
												}
											}
										}
									}
								}
							}
						}
					}
			}
			return opened;
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

		protected PreferencesHint getPreferencesHint() {
			return UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT;
		}

		protected String getDiagramKind() {
			return UMLDiagramEditor.ID;
			// return PackageEditPart.MODEL_ID;
		}

		protected String getEditorID() {
			return UMLClassDiagramJavaEditor.ID;
		}
	}

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