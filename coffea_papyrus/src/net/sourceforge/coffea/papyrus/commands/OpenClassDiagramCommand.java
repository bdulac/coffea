package net.sourceforge.coffea.papyrus.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.uml2.uml.Package;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.coffea.java.handlers.JavaModelServiceHandler;
import net.sourceforge.coffea.papyrus.CoffeaPapyrusPlugin;
import net.sourceforge.coffea.papyrus.editors.policies.OpenUMLClassDiagramJavaEditPolicy;
import net.sourceforge.coffea.papyrus.editors.policies.OpenUMLClassJavaEditPolicy;
import net.sourceforge.coffea.papyrus.handlers.PapyrusJavaModelServiceHandler;
import net.sourceforge.coffea.papyrus.providers.UMLClassViewProvider;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IASTNodeService;
import net.sourceforge.coffea.uml2.model.IAttributeService;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IClassifierService;
import net.sourceforge.coffea.uml2.model.IContainableElementService;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IMemberService;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.creation.IModelServiceBuilding;
import net.sourceforge.coffea.uml2.model.impl.ClassService;
import net.sourceforge.coffea.uml2.model.impl.OperationService;
import net.sourceforge.coffea.uml2.model.impl.PropertyService;
import net.sourceforge.coffea.uml2.model.impl.PackageService;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.impl.DiagramImpl;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.uml.diagram.clazz.edit.parts.PackageEditPart;
import org.eclipse.papyrus.uml.diagram.clazz.part.Messages;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditor;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.clazz.part.UMLDiagramEditorUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;

public class OpenClassDiagramCommand extends AbstractTransactionalCommand implements
		ISelectionListener, ISelectionProvider {

	public static UMLClassViewProvider clazzDiagViewProvider = new UMLClassViewProvider();

	protected ExecutionEvent event;

	protected Package context;

	/** Editor identifier */
	public static final String ID = Resources
			.getParameter("constants.editorId");

	/** Worker linking the UML model and the Java source */
	protected IModelServiceBuilding creator;

	/**
	 * Boolean value indicating if the editor has been correctly initialized
	 */
	protected boolean initialized;

	/**
	 * Table of listeners listening to diagram elements changes, the key for
	 * each element is its name
	 */
	protected Map<String, ResourceSetListener> listeners;

	/**
	 * Table of UML elements edit policies. Each element having only one edit
	 * policy, the key for this policy is the element itself
	 */
	protected Map<EditPart, EditPolicy> editPolicies;

	/** EMF editing domain */
	protected TransactionalEditingDomain editingDomain;

	/** UML edit part factory */
	protected EditPartFactory factory;

	/** Handler for the last selected element */
	protected IElementService lastSelectedElementHandler;

	/** Main package edit part */
	protected PackageEditPart mainPackagePart;

	private IEditorSite site;
	
	private HintedDiagramLinkStyle diagramFacet;

	/*
	 * public OpenClassDiagramCommand(ServicesRegistry servicesRegistry, Diagram
	 * diagram) throws ServiceException { super(servicesRegistry, diagram); }
	 */

	public OpenClassDiagramCommand(
			TransactionalEditingDomain domain,
			ExecutionEvent evt, 
			Package ctx, 
			HintedDiagramLinkStyle linkStyle
	) {
		super(domain, Messages.CommandName_OpenDiagram, null);
		context = ctx;
		event = evt;
		diagramFacet = linkStyle;
	}
	
	

	@Override
	protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	private EditPart getDiagramEditPart() {
		// TODO
		return null;
	}

	/** UML elements modifications listeners initialization */
	protected void initListeners() {
		EditPart p = getDiagramEditPart();
		if ((p != null) && (p instanceof IGraphicalEditPart)) {
			IGraphicalEditPart g = (IGraphicalEditPart) p;
			editingDomain = g.getEditingDomain();
			Diagram d = null;
			Object m = g.getModel();
			String name = null;
			if ((m != null) && (m instanceof Diagram)) {
				d = (Diagram) m;
				EObject eo = d.getElement();
				if (eo instanceof Package) {
					name = PackageService.buildFullyQualifiedName((Package) eo);

				} else if (eo instanceof Class) {
					Class cl = (Class) eo;
					name = ClassService.buildFullyQualifiedName(cl);
				}
				if (name != null) {
					TransactionalEditingDomain domain = g.getEditingDomain();
					if (listeners.get(name) == null) {
						IElementService elH = findHandler(g);
						if (elH != null) {
							domain.addResourceSetListener(elH);
							listeners.put(name, elH);
						}
					} else {
						domain = null;
					}
					listenToChildren(g, name);
				}
			}
			/*
			 * List<?> l = p.getChildren(); if(l!=null) { IGraphicalEditPart g =
			 * null; Node n = null; Object o = null; Object m = null; String
			 * name = null; for(int i=0 ; i<l.size() ; i++) { o = l.get(i);
			 * if((o!=null)&&(o instanceof IGraphicalEditPart)) { name = null; g
			 * = (IGraphicalEditPart)o; m = g.getModel(); if((m!=null)&&(m
			 * instanceof Node)) { n = (Node)m; EObject eo = n.getElement();
			 * if(eo instanceof Package) { name =
			 * PackageHandler.resolveFullyQualifiedName( (Package)eo );
			 * 
			 * } else if(eo instanceof Class) { Class cl = (Class)eo; name =
			 * ClassHandler.resolveFullyQualifiedName( cl ); } if(name!=null) {
			 * TransactionalEditingDomain domain = g.getEditingDomain();
			 * if(listeners.get(name)==null) { IElementHandling elH =
			 * findHandler(g); if(elH!=null) {
			 * domain.addResourceSetListener(elH); g.addEditPartListener(elH);
			 * listeners.put(name, elH); } } else { domain = null; }
			 * listenToChildren(g, name); } } } } }
			 */
		}
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
				PapyrusJavaModelServiceHandler.getPapyrusServiceLocator()
				.getModelService(win);
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
	
	protected PreferencesHint getPreferencesHint() {
		return UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT;
	}

	protected String getDiagramKind() {
		return UMLDiagramEditor.ID;
		// return PackageEditPart.MODEL_ID;
	}

	protected String getEditorID() {
		// return UMLClassDiagramJavaEditor.ID;
		return UMLDiagramEditor.ID;
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
									// UMLClassDiagramJavaEditor.ID, 
									UMLDiagramEditor.ID, 
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
											// UMLClassDiagramJavaEditor
											UMLDiagramEditor
									) {
										/*
										UMLClassDiagramJavaEditor umlEditor 
										= ((UMLClassDiagramJavaEditor)
													editor);
													*/
										UMLDiagramEditor umlEditor = 
												(UMLDiagramEditor)editor;
										IElementService elH = 
											getLastSelectedElementHandler();
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
													JavaModelServiceHandler
													.getServiceLocator()
													.getLastSourceViewId();
												opened = true;
												PapyrusJavaModelServiceHandler
												.getPapyrusServiceLocator()
												.getModelService(
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

	/**
	 * Returns an element handler from the model provided by {@link #creator}
	 * 
	 * @param name
	 *            Name of the element to return the handler for
	 * @return Element handler responding to the given element name
	 */
	protected IElementService getElementHandler(String name) {
		IModelService mdl = null;
		IElementService rt = null;
		if (creator != null) {
			mdl = creator.getLatestModelServiceBuilt();
			if (mdl != null) {
				rt = mdl.getElementService(name);
				if (rt == null) {
					IElementService defaultPackageElement = mdl
							.getElementService(IModelService.defaultPackageFileName);
					if (defaultPackageElement instanceof IPackageService) {
						rt = ((IPackageService) defaultPackageElement)
								.getElementService(name);
					}
				}
			}
		}
		return rt;
	}

	/**
	 * Ensures the correct edit policy has been installed on an edit part
	 * 
	 * @param p
	 *            Edit part on which the policy installation must be ensured
	 * @param elH
	 *            Element handler for which the installation could be done
	 */
	protected void ensureEditPolicy(EditPart p, IElementService elH) {
		if ((editPolicies != null) && (elH != null)) {
			// We try to get the element edit policy
			EditPolicy policy = editPolicies.get(p);
			// If we don't have a policy,
			if (policy == null) {
				// Then we try to install one
				if (elH instanceof IPackageService) {
					if (p instanceof EditPart) {
						EditPart packPart = (EditPart) p;
						((EditPart) packPart)
								.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
						policy = new OpenUMLClassDiagramJavaEditPolicy();
						packPart.installEditPolicy(EditPolicyRoles.OPEN_ROLE,
								policy);

					}
					/*
					 * else if(p instanceof Package2EditPart) { Package2EditPart
					 * packPart = (Package2EditPart)p;
					 * packPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassDiagramJavaEditPolicy();
					 * packPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof Package3EditPart) {
					 * Package3EditPart packPart = (Package3EditPart)p;
					 * packPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassDiagramJavaEditPolicy();
					 * packPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof Package4EditPart) {
					 * Package4EditPart packPart = (Package4EditPart)p;
					 * packPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassDiagramJavaEditPolicy();
					 * packPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof Package6EditPart) {
					 * Package6EditPart packPart = (Package6EditPart)p;
					 * packPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassDiagramJavaEditPolicy();
					 * packPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof PackageAsFrameEditPart)
					 * { PackageAsFrameEditPart packPart =
					 * (PackageAsFrameEditPart)p;
					 * packPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassDiagramJavaEditPolicy();
					 * packPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); }
					 */
				}
				if (elH instanceof IClassService<?, ?>) {
					IClassService<?, ?> clH = (IClassService<?, ?>) elH;
					if (p instanceof EditPart) {
						EditPart classPart = (EditPart) p;
						/*
						 * EditPart comment = factory.createEditPart( classPart,
						 * UMLElementTypes.Comment_2018 );
						 */
						classPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
						policy = new OpenUMLClassJavaEditPolicy(clH);
						classPart.installEditPolicy(EditPolicyRoles.OPEN_ROLE,
								policy);
					}
					/*
					 * else if(p instanceof Class2EditPart) { Class2EditPart
					 * classPart = (Class2EditPart)p;
					 * classPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassJavaEditPolicy(clH);
					 * classPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof Class3EditPart) {
					 * Class3EditPart classPart = (Class3EditPart)p;
					 * classPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassJavaEditPolicy(clH);
					 * classPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); } else if(p instanceof Class4EditPart) {
					 * Class4EditPart classPart = (Class4EditPart)p;
					 * 
					 * classPart.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
					 * policy = new OpenUMLClassJavaEditPolicy(clH);
					 * classPart.installEditPolicy( EditPolicyRoles.OPEN_ROLE,
					 * policy ); }
					 */
				}
				// If we have installed an edit policy,
				if (policy != null) {
					// Then we register it
					editPolicies.put(p, policy);
				}
			}
		} else {
			p.removeEditPolicy(EditPolicyRoles.OPEN_ROLE);
		}
	}

	/**
	 * Listens to and edit part children resources changes
	 * 
	 * @param p
	 *            Parent edit part which children must be listened to
	 * @param name
	 *            Parent edit part resource name
	 */
	protected void listenToChildren(EditPart p, String name) {
		List<?> children = p.getChildren();
		if (children != null) {
			Object child = null;
			EditPart childEditPart = null;
			String childName = null;
			NamedElement childElement = null;
			Node childModel = null;
			IElementService childH = null;
			for (int i = 0; i < children.size(); i++) {
				child = children.get(i);
				if ((child != null) && (child instanceof EditPart)) {
					childEditPart = (EditPart) child;
					if (childEditPart.getModel() instanceof Node) {
						childModel = (Node) childEditPart.getModel();
						if (childModel.getElement() instanceof NamedElement) {
							childElement = (NamedElement) childModel
									.getElement();
							childName = name
									+ Resources
											.getParameter("constants.editingFileNamePrefix")
									+ childElement.getName();
							childH = getElementHandler(childName);
							if ((childEditPart instanceof IGraphicalEditPart)
									&& (childH != null)) {
								IGraphicalEditPart graphicalPart = ((IGraphicalEditPart) childEditPart);
								TransactionalEditingDomain domain = graphicalPart
										.getEditingDomain();
								if (listeners.get(name) == null) {
									domain.addResourceSetListener(childH);
									listeners.put(name, childH);
								} else {
									domain = null;
								}
							}
						}
					}
					listenToChildren(childEditPart, childName);
				}
			}
		}
	}

	/**
	 * Sets {@link #creator}
	 * 
	 * @param w
	 *            Value of {@link #creator}
	 */
	public void setWorker(IModelServiceBuilding w) {
		creator = w;
		initListeners();
		initEditPolicy(getDiagramEditPart());
	}

	/**
	 * Returns {@link #lastSelectedElementHandler}
	 * 
	 * @return Value of {@link #lastSelectedElementHandler}
	 */
	public IElementService getLastSelectedElementHandler() {
		return lastSelectedElementHandler;
	}

	/**
	 * Initializes the edit policy for an edit part (recursive)
	 * 
	 * @param p
	 *            Edit part for which the edit policy must be initialized
	 */
	private void initEditPolicy(EditPart p) {
		if (p != null) {
			IElementService elH = findHandler(p);
			ensureEditPolicy(p, elH);
			if ((mainPackagePart == null) && (p instanceof PackageEditPart)) {
				mainPackagePart = (PackageEditPart) p;
			}
			/*
			 * if(elH instanceof IASTNodeHandling<?, ?>) { IASTNodeHandling<?,
			 * ?> nodeH = (IASTNodeHandling<?, ?>)elH; IJavaElement el =
			 * nodeH.getJavaElement(); if(el != null) { try { String doc =
			 * el.getAttachedJavadoc(new NullProgressMonitor()); if((doc !=
			 * null) && (mainPackagePart != null)) { NoteEditPart note = new
			 * NoteEditPart( mainPackagePart.getDiagramView() );
			 * mainPackagePart.getChildren().add(note); } } catch
			 * (JavaModelException e) { e.printStackTrace(); } } }
			 */
			List<?> children = p.getChildren();
			if (children != null) {
				Object o = null;
				for (int i = 0; i < children.size(); i++) {
					o = children.get(i);
					if (o instanceof EditPart) {
						initEditPolicy((EditPart) o);
					}
				}
			}
		}
	}

	/**
	 * Finds the element handler for an edit part
	 * 
	 * @param p
	 *            Edit part for which a handler must be found
	 * @return Handler corresponding to the given edit part
	 */
	public IElementService findHandler(EditPart p) {
		Class classSel = null;
		Property propSel = null;
		Operation opSel = null;
		Package packSel = null;
		String name = new String();
		// We get the underlying UML element in its specialized form
		if (p.getModel() instanceof Node) {
			Node model = (Node) p.getModel();
			if (model.getElement() instanceof Class) {
				classSel = (Class) model.getElement();
				name = ClassService.buildFullyQualifiedName(classSel);
			} else if (model.getElement() instanceof Operation) {
				opSel = (Operation) model.getElement();
				classSel = opSel.getClass_();
				name = OperationService.buildFullyQualifiedName(opSel);
			} else if (model.getElement() instanceof Property) {
				propSel = (Property) model.getElement();
				name = PropertyService.buildFullyQualifiedName(propSel);
			} else if (model.getElement() instanceof Package) {
				packSel = (Package) model.getElement();
				name = PackageService.buildFullyQualifiedName(packSel);
			}
		} else if (p.getModel() instanceof DiagramImpl) {
			EObject el = ((DiagramImpl) p.getModel()).getElement();
			if (el instanceof Package) {
				packSel = (Package) el;
				name = PackageService.buildFullyQualifiedName(packSel);
			}
		}
		// From this element, we resolve a full name
		/*
		 * if(classSel!=null) {
		 * 
		 * } else if(propSel!=null) { name =
		 * PropertyHandler.buildFullyQualifiedName(propSel); } else
		 * if(opSel!=null) { name += '#' + opSel.getName(); } else
		 * if(packSel!=null) { name =
		 * PackageHandler.buildFullyQualifiedName(packSel); }
		 */
		// From the element name we get the corresponding tool
		IElementService elH = getElementHandler(name);
		return elH;
	}

	/**
	 * Shows the java element in view specified by viewId
	 */
	/*
	 * @SuppressWarnings("restriction") public void show(final IJavaElement
	 * element, String viewId){ // get hold of the view javadoc/declaration view
	 * AbstractInfoView infoView = (AbstractInfoView)
	 * this.getSite().getPage().findView(viewId);
	 * 
	 * // get hold of the computeInput protected method, and invoke it. //
	 * returns the text to be set into the view. Method computeInput; try {
	 * computeInput = AbstractInfoView.class.getDeclaredMethod( "computeInput",
	 * Object.class ); computeInput.setAccessible(true); String source =
	 * (String) computeInput.invoke(infoView, element);
	 * 
	 * // set the IJavaElement into the view. Field fCurrentViewInput =
	 * AbstractInfoView.class.getDeclaredField("fCurrentViewInput");
	 * fCurrentViewInput.setAccessible(true); fCurrentViewInput.set(infoView,
	 * element);
	 * 
	 * // set the text into the view Method doSetInput =
	 * AbstractInfoView.class.getDeclaredMethod( "doSetInput", Object.class );
	 * doSetInput.setAccessible(true); doSetInput.invoke(infoView, source); }
	 * catch (SecurityException e) { e.printStackTrace(); } catch
	 * (NoSuchMethodException e) { e.printStackTrace(); } catch
	 * (IllegalArgumentException e) { e.printStackTrace(); } catch
	 * (IllegalAccessException e) { e.printStackTrace(); } catch
	 * (InvocationTargetException e) { e.printStackTrace(); } catch
	 * (NoSuchFieldException e) { e.printStackTrace(); } }
	 */

	/** Diagram and model files removal runnable */
	class FilesRemover implements IRunnableWithProgress {

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			IModelService mdl = creator.getLatestModelServiceBuilt();
			IResource modelResource = mdl.getClassDiagramEMFResource();
			IResource classDiagramResource = mdl
					.getClassDiagramWorkspaceResource();
			try {
				if (classDiagramResource != null) {
					classDiagramResource.delete(true, monitor);
				}
				if (modelResource != null) {
					modelResource.delete(true, monitor);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}

	}

	/*
	 * @Override public void closeEditor(boolean save) {
	 * if((listeners!=null)&&(editingDomain!=null)) { Iterator<String> domains =
	 * listeners.keySet().iterator(); String domain = null;
	 * while(domains.hasNext()) { domain = domains.next(); if(domain!=null) {
	 * ResourceSetListener listener = listeners.get(domain);
	 * editingDomain.removeResourceSetListener(listener); } } }
	 * if(editPolicies!=null) { Iterator<EditPart> parts =
	 * editPolicies.keySet().iterator(); EditPart part = null; EditPolicy policy
	 * = null; while(parts.hasNext()) { part = parts.next(); if(part!=null) {
	 * policy = editPolicies.get(part); if(policy!=null) {
	 * part.removeEditPolicy(policy); } } } } super.closeEditor(save); }
	 */

	@Override
	public void dispose() {
		super.dispose();
		if ((listeners != null) && (editingDomain != null)) {
			Iterator<String> domains = listeners.keySet().iterator();
			String domain = null;
			while (domains.hasNext()) {
				domain = domains.next();
				if (domain != null) {
					ResourceSetListener listener = listeners.get(domain);
					editingDomain.removeResourceSetListener(listener);
				}
			}
		}
		if (editPolicies != null) {
			Iterator<EditPart> parts = editPolicies.keySet().iterator();
			EditPart part = null;
			EditPolicy policy = null;
			while (parts.hasNext()) {
				part = parts.next();
				if (part != null) {
					policy = editPolicies.get(part);
					if (policy != null) {
						part.removeEditPolicy(policy);
					}
				}
			}
		}
		try {
			IModelService mdl = creator.getLatestModelServiceBuilt();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			creator.getSourceWorkbenchWindow().run(false, false,
					new FilesRemover());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// ? EditorPart#getSite() hidden ?
	private IEditorSite getSite() {
		return site;
	}

	// @Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// super.selectionChanged(part, selection);
		if ((part.equals(getSite().getPart())) && (creator != null)) {
			IWorkbenchWindow window = creator.getSourceWorkbenchWindow();
			// If we have a structured selection,
			if ((selection instanceof StructuredSelection) && (window != null)) {
				IWorkbenchPage page = window.getActivePage();
				StructuredSelection sel = (StructuredSelection) selection;
				// Then we get the first selected element
				Object first = sel.getFirstElement();
				// If it is a graphical edit part,
				if (first instanceof EditPart) {
					// Then we specialize it and try to get a handler for this
					// edit part
					EditPart p = (EditPart) first;
					lastSelectedElementHandler = findHandler(p);
					ensureEditPolicy(p, lastSelectedElementHandler);
					// If we have a handler
					if ((lastSelectedElementHandler != null)) {
						IType tp = null;
						IJavaElement el = null;
						if (lastSelectedElementHandler instanceof IMemberService<?, ?>) {
							// The we try to specialize it and display the
							// source
							IMemberService<?, ?> memH = (IMemberService<?, ?>) lastSelectedElementHandler;
							IClassifierService<?, ?> tpH = null;
							IElementService cont = memH;
							while ((!(cont instanceof IClassifierService<?, ?>))
									&& (cont instanceof IContainableElementService<?, ?>)) {
								cont = ((IContainableElementService<?, ?>) cont)
										.getContainerService();
							}
							if (cont instanceof IClassifierService<?, ?>) {
								tpH = (IClassifierService<?, ?>) cont;
								try {
									tp = tpH.getModelService().getJavaProject()
											.findType(tpH.getFullName());
									if ((memH instanceof IAttributeService)
											&& (tp != null)) {
										el = tp.getField(memH.getSimpleName());
									} else if ((memH instanceof IMethodService)
											&& (tp != null)) {
										// el = tp.getM;
									} else if (memH.equals(tpH)) {
										el = tpH.getJavaElement();
									} else if (memH instanceof IClassifierService<?, ?>) {
										// el = tpH;
									}
								} catch (JavaModelException e) {
									CoffeaPapyrusPlugin.getInstance().logError(
											"Unable find Java Element for "
													+ memH, e);
								}
							}
						} else if (lastSelectedElementHandler instanceof IPackageService) {
							IPackageService packH = ((IPackageService) lastSelectedElementHandler);
							IModelService modelH = packH.getModelService();
							if ((modelH != null)
							/*
							 * &&( !modelH.getFullName().equals(
							 * packH.getFullName() ) )
							 */
							) {
								el = packH.getJavaElement();
							}
						}
						StructuredSelection javaSelection = null;
						IWorkbenchPart vPart = null;
						if (el != null) {
							javaSelection = new StructuredSelection(el);
							vPart = page.findView(creator.getSourceViewId());
							// show(el, org.eclipse.ui.internal.views.);
							if ((vPart != null)
									&& (!vPart.getSite().getSelectionProvider()
											.getSelection()
											.equals(javaSelection))) {
								vPart.getSite().getSelectionProvider()
										.setSelection(javaSelection);
								// vPart.setFocus();
								// this.getDiagramEditPart().activate();
								// vPart.setFocus();
								/*
								 * vPart.getSite().getSelectionProvider()
								 * .setSelection( javaSelection );
								 * vPart.setFocus();
								 */
								/*
								 * vPart.getSite(). vPart.setFocus(); try {
								 * wait(200); } catch (InterruptedException e) {
								 * e.printStackTrace(); } this.setFocus();
								 */
							}
						}
					}
				}
			}
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {

	}

	public ISelection getSelection() {
		ISelection sel = null;
		if (lastSelectedElementHandler instanceof IASTNodeService<?, ?>) {
			IASTNodeService<?, ?> astNodeH = (IASTNodeService<?, ?>) lastSelectedElementHandler;
			new StructuredSelection(astNodeH.getJavaElement());
		}
		return sel;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {

	}

	public void setSelection(ISelection selection) {
		this.setSelection(selection);
	}
}