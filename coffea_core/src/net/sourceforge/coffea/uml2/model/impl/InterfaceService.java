package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IGroupService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.IOwnerService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.ITypesContainerService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename
.RenameCompilationUnitProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Realization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

/** Service for an interface */
public class InterfaceService<C extends Classifier> 
extends ClassifierService<C, TypeDeclaration, IType>
implements IInterfaceService<TypeDeclaration, IType>{

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -1693022755248903916L;

	/** 
	 * Compilation unit parsed from source code and owning the type handled by 
	 * the service
	 * @see #syntaxTreeNode
	 */
	protected CompilationUnit parsedUnit;

	/** 
	 * Compilation unit processed from java model and owning the 
	 * type handled by the service
	 * @see #javaElement
	 */
	protected ICompilationUnit processedUnit;

	/** List of super interfaces names */ 
	protected List<String> superInterfacesNames;

	/** List of super interfaces handlers */
	protected List<IInterfaceService<?, ?>> superInterfaces;

	/**
	 * List of services for the dependencies from which the type is 
	 * client
	 */
	protected List<CompositionService> dependenciesServices;

	/** List services for the methods belonging to the handled type */
	protected List<IMethodService> operationsServices;

	/**
	 * Interface service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	public InterfaceService(ITypesContainerService p, String nm) {
		super(p, nm);
		completeConstruction(null, p);
	}

	/**
	 * Interface service construction without any declaration but with and 
	 * an existing UML element
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	public InterfaceService(ITypesContainerService p, String nm, C c) {
		super(p, nm, c);
		completeConstruction(null, p);
	}

	/**
	 * Interface service construction from an AST node
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 * @param r
	 * Value of {@link #rewriter}
	 * @param u
	 * Value of {@link #parsedUnit}
	 */
	public InterfaceService(
			TypeDeclaration stxNode, 
			ITypesContainerService p, 
			ASTRewrite r, 
			CompilationUnit u
	) {
		super(stxNode, p);
		completeConstruction(r, p, u);
	}

	/**
	 * Interface service construction from an AST node and an existing UML 
	 * element
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 * @param u
	 * Value of {@link #parsedUnit}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	public InterfaceService(
			TypeDeclaration stxNode, 
			ITypesContainerService p, 
			CompilationUnit u, 
			C c
	) {
		super(stxNode, p, c);
		completeConstruction(null, p, u);
	}

	/**
	 * Interface service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param u
	 * Value of {@link #processedUnit}
	 */
	public InterfaceService(
			IType jEl, 
			ITypesContainerService p, 
			ICompilationUnit u
	) {
		super(jEl, p);
		completeConstruction(null, p, u);
	}

	/**
	 * Interface service construction from a Java element and an existing UML 
	 * element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param r
	 * Value of {@link #rewriter}
	 * @param u
	 * Value of {@link #processedUnit}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	public InterfaceService(
			IType jEl, 
			ITypesContainerService p,
			ASTRewrite r, 
			ICompilationUnit u, 
			C c
	) {
		super(jEl, p, c);
		completeConstruction(r, p, u);
	}

	//Completes the constructors, factorization of the specialized part for 
	//use in every constructor
	protected void completeConstruction(
			ASTRewrite r, 
			IOwnerService p
	) {
		if(p instanceof ITypesContainerService) {
			ITypesContainerService cont =
				(ITypesContainerService)p;
			cont.addTypeService(this);
		}
		this.rewriter = r;
		superInterfacesNames = new ArrayList<String>();
		List<?> interfaces = null;
		this.operationsServices = new ArrayList<IMethodService>();
		if(syntaxTreeNode != null) {
			// We get all the methods declarations
			MethodDeclaration[] operations = syntaxTreeNode.getMethods();
			// We add a handler to the list for each method of the class
			for (int i = 0 ; i < operations.length ; i++) {
				addOperationService(
						new OperationService(operations[i], this)
				);
			}
			interfaces = syntaxTreeNode.superInterfaceTypes();
			// If this class has super interfaces, 
			if(interfaces != null) {
				// Then we try to resolves the binding for each interface, 
				Object ob = null;
				Type tp = null;
				ITypeBinding binding = null;
				for(int i = 0 ; i < interfaces.size() ; i++) {
					ob = interfaces.get(i);
					if((ob != null) && (ob instanceof Type)) {
						tp = (Type)ob;
						binding = tp.resolveBinding();
						if(binding != null) {
							// Aiming to get a qualified name
							superInterfacesNames.add(
									binding.getQualifiedName()
							);
						}
					}
				}
			}
		}
		else if(javaElement != null) {
			try {
				IMethod[] operations = javaElement.getMethods();
				if(operations != null) {
					// We add a handler to the list for each method of the 
					// class
					for (int i = 0 ; i < operations.length ; i++) {
						addOperationService(
								new OperationService(operations[i], this)
						);
					}
				}
				String[] superIntNames = 
					javaElement.getSuperInterfaceNames();
				if(superIntNames != null) {
					for(int i = 0 ; i < superIntNames.length ; i++) {
						String[][] parts = 
							javaElement.resolveType(superIntNames[i]);
						String name = nameReconstruction(parts);
						if(name!=null) {
							superInterfacesNames.add(name);
						}
					}
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}
		}
	}

	protected void completeConstruction(
			ASTRewrite r, 
			IOwnerService p, 
			ICompilationUnit c
	) {
		processedUnit = c;
		completeConstruction(r, p);
	}

	protected void completeConstruction(
			ASTRewrite r, 
			IOwnerService p, 
			CompilationUnit c
	) {
		parsedUnit = c;
		completeConstruction(r, p);
	}

	// @Override
	public ICompilationUnit getCompilationUnit() {
		return processedUnit;
	}

	// @Override
	public CompilationUnit getParsedUnit() {
		return parsedUnit;
	}

	// @Override
	public String nameReconstruction(String[][] nm) {
		String name = null;
		if(nm != null) {
			String[] nameParts = nm[0];
			if(nameParts != null) {
				name = new String();
				for(int i=0 ; i < nameParts.length ; i++) {
					if(name.length() > 0) {
						name += '.';
					}
					if(nameParts[i] != null) {
						name += nameParts[i];
					}
				}
			}
		}
		return name;
	}

	@Override
	public ITypesContainerService getContainerService() {
		return (ITypesContainerService)container;
	}

	// @Override
	public ASTRewrite getRewriter() {
		return this.rewriter;
	}

	/**
	 * Returns {@link #dependenciesServices}Interfaces
	 * @return Value of {@link #dependenciesServices}
	 */
	public List<CompositionService> getDependenciesServices() {
		return this.dependenciesServices;
	}
	
	@SuppressWarnings("unchecked")
	private void loadExistingUmlElement() {
		ITypesContainerService cont = getContainerService();
		Element contEl = cont.getUMLElement();
		if(contEl instanceof Package) {
			Package pack = (Package)contEl;
			NamedElement el = pack.getMember(getSimpleName());
			if(el instanceof Classifier) {
				umlModelElement = (C)el;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createUmlElement() {
		IGroupService parent = getContainerService();
		String name = null;
		if((syntaxTreeNode != null)&&(syntaxTreeNode.getName() != null)) {
			name = syntaxTreeNode.getName().getFullyQualifiedName();
		}
		else if(javaElement != null) {
			name = javaElement.getTypeQualifiedName();
		}
		else if(defaultSimpleName != null) {
			name = getSimpleName();
		}
		if(name != null) {
			try {
				if(parent instanceof IPackageService) {
					IPackageService pack = (IPackageService)parent;
					if((javaElement != null) && (javaElement.isEnum())) {
						umlModelElement = 
								(C)UMLFactory.eINSTANCE.createEnumeration();
						umlModelElement.setName(name);
						pack.getUMLElement().getOwnedMembers()
						.add(umlModelElement);
					}
					else {
						umlModelElement = 
								(C) pack.getUMLElement().createOwnedClass(
										name, 
										isAbstract()
								);
					}
				} 
				else if(parent instanceof IClassService<?, ?>) {
					IClassService<?, ?> nestingClass = 
							(IClassService<?, ?>)parent;
					if((javaElement != null) && (javaElement.isEnum())) {
						umlModelElement = 
								(C)UMLFactory.eINSTANCE.createEnumeration();
						umlModelElement.setName(name);
						Classifier cl = nestingClass.getUMLElement();
						if(cl instanceof org.eclipse.uml2.uml.Class) {
							org.eclipse.uml2.uml.Class cla = 
									(org.eclipse.uml2.uml.Class)cl;
							cla.getNestedClassifiers()
							.add(umlModelElement);
						}
						else if(cl instanceof Enumeration) {
							Enumeration en = (Enumeration)cl;
							en.getMembers().add(umlModelElement);
						}
					}
					else {
						umlModelElement = 
								(C) UMLFactory.eINSTANCE.createClass();
						int indDollar = -1;
						if ((indDollar = name.indexOf('$')) >= 0) {
							name = name.substring(indDollar + 1);
						}
						umlModelElement.setName(name);
						Classifier cl = nestingClass.getUMLElement();
						if(cl instanceof org.eclipse.uml2.uml.Class) {
							org.eclipse.uml2.uml.Class cla = 
									(org.eclipse.uml2.uml.Class)cl;
							cla.getNestedClassifiers()
							.add(umlModelElement);
						}
						else if(cl instanceof Enumeration) {
							Enumeration en = (Enumeration)cl;
							en.getMembers().add(umlModelElement);
						}
					}
				}
				else if(parent instanceof IModelService) {
					IModelService md = (IModelService)parent;
					if((javaElement != null) && (javaElement.isEnum())) {
						umlModelElement = 
								(C)UMLFactory.eINSTANCE.createEnumeration();
						umlModelElement.setName(name);
						md.getUMLElement().getOwnedMembers()
						.add(umlModelElement);
					}
					else {
						umlModelElement = 
								(C)md.getUMLElement().createOwnedClass(
										name, 
										isAbstract()
								);
					}
				}
			} catch(JavaModelException e) {
			}
		}
		if(syntaxTreeNode != null) {
			Javadoc doc = syntaxTreeNode.getJavadoc();
			if(doc != null) {
				Comment docComment = 
					umlModelElement.createOwnedComment();
				docComment.setBody(doc.toString());				
			}
		}
	}

	// @Override
	public void setUpUMLModelElement() {
		if(umlModelElement == null)loadExistingUmlElement();
		if(umlModelElement == null) {
			createUmlElement();
			setupSuperTypeUMLModelElement();
			for(int i=0 ; i<operationsServices.size() ; i++) {
				operationsServices.get(i).setUpUMLModelElement();
			}
		}
	}

	protected void setupSuperTypeUMLModelElement() {
		/*
		String superInter = null;
		IInterfaceHandling interH = null;
		superInterfaces = new ArrayList<IInterfaceHandling>();
		for(int i=0 ; i<superInterfacesNames.size() ; i++) {
			superInter = superInterfacesNames.get(i);
			if(superInter!=null) {
				interH = 
					CoffeeWorker.getWorker().getModelHandler()
					.getInterfaceHandler(
						superInter
					);
				if(interH!=null) {
					superInterfaces.add(interH);
					getUMLElement().createGeneralization(
							interH.getUMLElement()
					);
				}
			}
		}
		 */
		if((superInterfacesNames!=null)&&(superInterfacesNames.size()>0)) {
			String superInterfaceName;
			for(int i=0 ; i<superInterfacesNames.size() ; i++) {
				superInterfaceName = superInterfacesNames.get(i);
				ITypeService<?, ?> general = 
					getContainerService().resolveTypeService(
							superInterfaceName
					);
				if(
						(general!=null)
						&&(general.getUMLElement() instanceof Classifier)
				) {
					Classifier elGeneral = (Classifier)general.getUMLElement();
					Element cont = getContainerService().getUMLElement();
					if(cont instanceof Package) {
						Package pack = (Package)cont;
						Element pEl = 
							pack.createPackagedElement(
									null, 
									UMLPackage.eINSTANCE.getRealization()
							);
						if(pEl instanceof Realization) {
							Realization real = (Realization)pEl;
							real.getClients().add(elGeneral);
							real.getSuppliers().add(this.getUMLElement());
						}
					}
				}
			}
		}
	}
	
	// @Override
	public String getSimpleName() {
		String name = null;
		if((syntaxTreeNode!=null) &&(syntaxTreeNode.getName()!=null)){
			name = syntaxTreeNode.getName().toString();
		}
		else if(javaElement!=null){
			name =  javaElement.getElementName();
		}
		else if(defaultSimpleName!=null) {
			name = defaultSimpleName;
		}
		return name;
	}

	// @Override
	public List<IMethodService> getOperationsServices() {
		return operationsServices;
	}

	// @Override
	public void addOperationService(IMethodService opH) {
		operationsServices.add(opH);
	}

	// @Override
	public IMethodService getOperationService(String n) {
		if(n!=null) {
			List<IMethodService> operations = getOperationsServices();
			for(int i=0 ; i<operations.size() ; i++) {
				if(operations.get(i).getFullName().equals(n))
					return operations.get(i);
			}
		}
		return null;
	}

	// @Override
	public IElementService getElementService(String n) {
		return getOperationService(n);
	}

	// @Override
	public IElementService getElementHandler(Element el) {
		IElementService elH = null;
		if(el!=null) {
			String elFullName = ElementService.buildFullyQualifiedName(el);
			if(elFullName!=null) {
				getElementService(elFullName);
			}
		}
		return elH;
	}

	// @Override
	public List<IElementService> getElementsHandlers() {
		List<IElementService> ret = new ArrayList<IElementService>();
		if(operationsServices!=null) {
			IMethodService op = null;
			for(int i=0 ; i<operationsServices.size() ; i++) {
				op = operationsServices.get(i);
				if(op!=null) {
					ret.add(op);
				}
			}
		}
		return ret;
	}

	// @Override
	public void setContainerService(ITypesContainerService gr) {
		container = gr;
	}

	// @Override
	public IMethodService createOperation(Operation o) {
		return null;
	}

	// @Override
	public void deleteOperation(Operation o) {
	}

	@Override
	public void rename(String nm) {
		RenamingRunnable runnable = new RenamingRunnable(nm);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	/** Renaming runnable */
	public class RenamingRunnable implements IUML2RunnableWithProgress {

		/** New simple name */
		protected String newName;

		/**
		 * Renaming runnable construction
		 * @param nm
		 * Value of {@link #newName}
		 */
		public RenamingRunnable(String nm) {
			newName = nm;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if((javaElement!=null)&&(newName!=null)) {
				if((processedUnit!=null)||(javaElement!=null)) {
					JavaRenameProcessor p;
					try {
						if(container instanceof IPackageService) {
							p = 
								new RenameCompilationUnitProcessor(
										processedUnit
								);
						}
						else {
							p = new RenameTypeProcessor(javaElement);
						}
						p.setNewElementName(newName);
						Refactoring r = new RenameRefactoring(p);
						PerformRefactoringOperation op = 
							new PerformRefactoringOperation(
									r, 
									CheckConditionsOperation.FINAL_CONDITIONS
							);
						op.run(monitor);
						ITypesContainerService contH = getContainerService();
						if(contH!=null) {
							String newFullName = contH.getFullName();
							newFullName += '.' + newName;
							IType tp = null;
							if(processedUnit!=null) {
								tp =
									processedUnit.getJavaProject().findType(
											newFullName
									);
							}
							else {
								tp =
									javaElement.getJavaProject().findType(
											newFullName
									);
							}
							if(tp!=null) {
								ICompilationUnit newUnit = 
									tp.getCompilationUnit();
								if(newUnit!=null) {
									ITypeService<?, ?> tpH = 
										getModelService().getServiceBuilder()
										.processTypeService(
												newUnit, 
												new NullProgressMonitor()
										);
									if(tpH!=null) { 
										contH.addTypeService(tpH);
										contH.getTypesServices().remove(this);
									}
								}
							}
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// @Override
	public Element findEditorUMLElement() {
		return umlModelElement;
	}

	// @Override
	public void acceptModelChangeNotification(Notification nt) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	public NotificationFilter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		// TODO Auto-generated method stub
	}
}