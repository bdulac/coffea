package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IAssociationService;
import net.sourceforge.coffea.uml2.model.IAttributeService;
import net.sourceforge.coffea.uml2.model.IClassifierService;
import net.sourceforge.coffea.uml2.model.IElementService;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameCompilationUnitProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Realization;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

/**
 * Service for a classifier
 * @param <E>
 * Type of the classifier handled by the service as UML element
 * @param <S> 
 * Type of the classifier handled by the service as AST node
 * @param <J>
 * Type of the classifier handled by the service as Java element
 */
public abstract class ClassifierService
<E extends Classifier, S extends TypeDeclaration, J extends IType> 
extends MemberService<E, S, J> 
implements IClassifierService<S, J> {
	
	/** @see java.io.Serializable */
	private static final long serialVersionUID = -951861172936464096L;

	/**
	 * Builds an UML type fully qualified name
	 * @param tp
	 * UML type for which the fully qualified name must be resolved
	 * @return UML type fully qualified name
	 */
	public static String buildFullyQualifiedName(Type tp) {
		String name = new String();
		if(tp!=null) {
			EObject container = tp.eContainer();
			String nestingClassName = new String();
			while(
					(!(container instanceof Package))
					&& (container instanceof NamedElement)
			) {
				nestingClassName = 
					((NamedElement)container).getName() +'$' 
					+ nestingClassName;
				container = container.eContainer();
			}
			Package pack = tp.getPackage();
			if((pack==null)&&(container instanceof Package)) {
				pack = (Package)container;
			}
			while(pack!=null) {
				if(
						(pack instanceof Model)
						&&(pack.getName() != null)
						&&(pack.getName().length() > 0)
						&&(
								!pack.getName().equals(
										IModelService.defaultPackageFileName
								)
						)
				) {
					name = pack.getName() + '.' + name;
					pack = null;
				}
				else if(
						(pack instanceof Package)
						&&(pack.getName() != null)
						&&(pack.getName().length() > 0)
						&&(
								!pack.getName().equals(
										IModelService.defaultPackageFileName
								)
						)
				) {
					name = pack.getName() + '.' + name;
					pack = pack.getNestingPackage();
				}
				else {
					pack = pack.getNestingPackage();
				}
			}
			name += nestingClassName + tp.getName();
		}
		return name;
	}
	
	/**
	 * Builds a Java type fully qualified name
	 * @param element
	 * Type to build the full name from
	 * @return Type fully qualified name
	 */
	public static String buildFullyQualifiedName(IType element) {
		String fullName = null;
		if (element != null) {
			IJavaElement parent = element.getParent();
			if(parent instanceof ICompilationUnit) {
				parent = parent.getParent();
			}
			if(parent instanceof IPackageFragment) {
				IPackageFragment pack = (IPackageFragment)parent;
				fullName = PackageService.buildFullyQualifiedName(pack);
				if(
						(fullName!=null)
						&&(fullName.equals(IModelService.defaultPackageFileName))
				) {
					fullName = new String();
				}
				else if(fullName!=null) {
					fullName += '.';
				}
			}
			else if(parent instanceof IType) {
				fullName = buildFullyQualifiedName((IType)parent);
				fullName += '.';
			}
			fullName += element.getElementName();
		}
		return fullName;
	}
	
	/**
	 * Builds an AST type declaration fully qualified name
	 * @param element
	 * Type declaration to build the full name from
	 * @return Type fully qualified name
	 */
	public static String buildFullyQualifiedName(TypeDeclaration element) {
		String fullName = null;
		if (element != null) {
			ASTNode parent = element.getParent();
			if(parent instanceof PackageDeclaration) {
				PackageDeclaration pack = (PackageDeclaration)parent;
				fullName = PackageService.buildFullyQualifiedName(pack) + '.';
			}
			else if(parent instanceof TypeDeclaration) {
				fullName = buildFullyQualifiedName((TypeDeclaration)parent);
				fullName += '$';
			}
			fullName += element.getName();
		}
		return fullName;
	}
	
	/**
	 * Java type simple name extraction from the corresponding UML element
	 * @param t
	 * UML element from which the name will be extracted
	 * @return Extracted simple name
	 */
	public static String simpleNameExtraction(Type t) {
		String simpleName = null;
		if(t!=null) {
			simpleName = t.getName();
		}
		return simpleName;
	}	
	
	/** Rewriter for the compilation unit the classifier belongs to */
	protected ASTRewrite rewriter;

	/** List of super interfaces handlers */
	private List<IInterfaceService<?, ?>> superInterfacesServices;

	/** Super type handler */
	private ITypeService<?, ?> superTypeService;

	/**
	 * List of services for the dependencies from which the type is 
	 * client
	 */
	private List<IAssociationService<?, ?>> dependenciesServices;

	/** List services for the methods belonging to the handled type */
	private List<IMethodService> operationsServices;
	
	/** List of properties belonging to the handled class */
	private List<IAttributeService> properties;
	
	/** List of nested classes belonging to the handled class */
	private List<ITypeService<?, ?>> types;
	
	/** 
	 * Compilation unit parsed from source code and owning the type handled by 
	 * the service
	 * @see #syntaxTreeNode
	 */
	private CompilationUnit parsedUnit;

	/** 
	 * Compilation unit processed from java model and owning the 
	 * type handled by the service
	 * @see #javaElement
	 */
	private ICompilationUnit processedUnit;
	
	/**
	 * Classifier service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	protected ClassifierService(ITypesContainerService p, String nm) {
		super(p, nm);
		completeConstruction(null, p);
	}
	
	/**
	 * Classifier service construction from an AST node
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 */
	protected ClassifierService(
			S stxNode,
			ASTRewrite r, 
			ITypesContainerService p, 
			CompilationUnit u
	) {
		super(stxNode, p);
		completeConstruction(r, p, u);
		completeClassConstruction(r, p, u);
	}
	
	/**
	 * Classifier service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 */
	protected ClassifierService(
			J jEl,
			ITypesContainerService p, 
			ICompilationUnit u
	) {
		super(jEl, p);
		completeConstruction(null, p, u);
		completeClassConstruction(null, p, u);
	}

	protected void completeClassConstruction(ASTRewrite r,
			ITypesContainerService p, ICompilationUnit c) {
		processedUnit = c;
	}

	protected void completeClassConstruction(ASTRewrite r,
			ITypesContainerService p, CompilationUnit c) {
		parsedUnit = c;
	}

	// Completes the constructors, factorization of the specialized part for
	// use in every constructor
	protected void completeConstruction(ASTRewrite r, IOwnerService p) {
		if (p instanceof ITypesContainerService) {
			ITypesContainerService cont = (ITypesContainerService) p;
			cont.addTypeService(this);
		}
		rewriter = r;
		operationsServices = new ArrayList<IMethodService>();
		types = new ArrayList<ITypeService<?, ?>>();
		getSuperInterfaceServices();
		getSuperTypeService();
		// Properties and dependencies
		dependenciesServices = new ArrayList<IAssociationService<?, ?>>();
		properties = new ArrayList<IAttributeService>();
		// AST Node
		if(syntaxTreeNode != null) {
			// We get all the attribute declarations
			FieldDeclaration[] fields = syntaxTreeNode.getFields();
			// For each attribute of the class,
			for (int i = 0; i < fields.length; i++) {
				// We add a tool to the list
				addPropertyService(new PropertyService(fields[i], this));
				// And a dependency
				dependenciesServices
						.add(new CompositionService(fields[i], this));
			}
		} else if (javaElement != null) {
			// We get all the fields
			try {
				IField[] fields = javaElement.getFields();
				// For each field of the class,
				for (int i = 0; i < fields.length; i++) {
					// We add a tool to the list
					addPropertyService(new PropertyService(fields[i], this));
					// And a dependency
					dependenciesServices.add(new CompositionService(fields[i],
							this));
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private ITypeService<?, ?> getSuperTypeService() {
		if(superTypeService == null) {
			String superClassName = null;
			// AST Node
			if (syntaxTreeNode != null) {

				// If this class has a super class,
				if (syntaxTreeNode.getSuperclassType() != null) {
					// We resolve this super class
					ITypeBinding binding = syntaxTreeNode.getSuperclassType()
							.resolveBinding();
					// And try to get its name
					if (binding != null) {
						superClassName = binding.getQualifiedName();
						int smtIdx = superClassName.indexOf('<');
						if (smtIdx >= 0) {
							superClassName = superClassName.substring(0, smtIdx);
						}
					}
				}
				// Java element
			} else if (javaElement != null) {
				try {
					// If this class has a super class,
					if (javaElement.getSuperclassName() != null) {
						// The we note its name
						String superSimpleName = javaElement.getSuperclassName();
						String[][] namesParts = 
								javaElement.resolveType(superSimpleName);
						String name = nameReconstruction(namesParts);
						if (name != null) {
							superClassName = name;
						}
					}
					/*
					 * String[] superIntNames =
					 * javaElement.getSuperInterfaceNames(); if (superIntNames !=
					 * null) { for (int i = 0; i < superIntNames.length; i++) {
					 * String[][] parts = javaElement
					 * .resolveType(superIntNames[i]); String name =
					 * nameReconstruction(parts); if (name != null) {
					 * superInterfacesNames.add(name); } } }
					 */
				} catch (JavaModelException e) {
					CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
				}
			}
			if(superClassName != null) {
				superTypeService = 
						getModelService().resolveTypeService(superClassName);
			}
		}
		return superTypeService;
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
	
	/**
	 * Adds an service to the list of services handling the properties 
	 * belonging to the handled owner
	 * @param prH
	 * New service to add to the list
	 */
	public void addPropertyService(IAttributeService prH) {
		properties.add(prH);
	}
	
	// @Override
	public List<IAttributeService> getPropertiesServices() {
		return properties;
	}
	
	// @Override
	public IAttributeService getPropertyService(String n) {
		if(n!=null) {
			List<IAttributeService> props = getPropertiesServices();
			for(int i=0 ; i<props.size() ; i++) {
				if(props.get(i).getFullName().equals(n))
					return props.get(i);
			}
		}
		return null;
	}
	
	public List<ITypeService<?, ?>> getTypesServices() {
		return types;
	}

	public void addTypeService(ITypeService<?, ?> clH) {
		types.add(clH);	
	}

	public ITypeService<?, ?> resolveTypeService(String n) {
		if(n != null) {
			if(n.equals(this.getFullName()))return this;
			List<ITypeService<?, ?>> cls = getTypesServices();
			for(int i = 0 ; i < cls.size() ; i++) {
				if(cls.get(i).getFullName().equals(n))return cls.get(i);
			}
		}
		return null;
	}
	
	@Override
	public void rename(String nm) {
		RenamingRunnable runnable = new RenamingRunnable(nm);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}
	
	// @Override
	public void deleteProperty(Property p) {
		if(p!=null) {
			PropertyRemoval runnable = new PropertyRemoval(p);
			CoffeaUML2Plugin.getInstance().execute(runnable);
		}
	}
	
	// @Override
	public IAttributeService createProperty(Property p) {
		IAttributeService a = null;
		if(p!=null) {
			PropertyCreation runnable = new PropertyCreation(p, this);
			CoffeaUML2Plugin.getInstance().execute(runnable);
			a  = runnable.getResult();
		}
		return a;
	}
	
	// @Override
	public IElementService getElementService(String n) {
		IElementService ret = getOperationService(n);
		if(ret != null)return ret;
		if(n!=null) {
			if((ret == null) && (properties != null)) {
				IAttributeService prop;
				for(int i = 0 ; i < properties.size() ; i++) {
					prop = properties.get(i);
					if(prop != null) {
						if(n.equals(prop.getFullName())) {
							ret = prop;
						}
						if(ret != null) {
							break;
						}
					}
				}
			}
			if((ret == null) && (operationsServices != null)) {
				IMethodService op;
				for(int i = 0 ; i < operationsServices.size() ; i++) {
					op = operationsServices.get(i);
					if(op != null) {
						if(n.equals(op.getFullName())) {
							ret = op;
						}
						if(ret != null) {
							break;
						}
					}
				}
			}
			if ((ret == null) && (types != null)) {
				ITypeService<?, ?> cl;
				for (int i = 0; i < types.size(); i++) {
					cl = types.get(i);
					if (cl != null) {
						if (n.equals(cl.getFullName())) {
							ret = cl;
						} else {
							ret = cl.getElementService(n);
						}
						if (ret != null) {
							break;
						}
					}
				}
			}
			return ret;
		}
		return ret;
	}

	// @Override
	public List<IMethodService> getOperationsServices() {
		return operationsServices;
	}

	/**
	 * Adds a service to the list of services handling the operations 
	 * belonging to the handled owner
	 * @param methodService
	 * New service to add to the list
	 */
	public void addOperationService(IMethodService opH) {
		operationsServices.add(opH);
	}

	/**
	 * Returns {@link #dependenciesServices}Interfaces
	 * 
	 * @return Value of {@link #dependenciesServices}
	 */
	public List<IAssociationService<?, ?>> getDependenciesServices() {
		return this.dependenciesServices;
	}
	
	@Override
	public ITypesContainerService getContainerService() {
		return (ITypesContainerService)container;
	}
	
	@Override
	public String getFullName() {
		String fullName = null;
		if(javaElement != null) {
			fullName = buildFullyQualifiedName(javaElement);
		}
		else if (syntaxTreeNode!=null) {
			fullName = buildFullyQualifiedName(syntaxTreeNode);
		}
		else {
			fullName = super.getFullName();
		}
		return fullName;
	}
	
	protected void setupSuperTypeUMLModelElement() {
		ITypeService<?, ?> superTpeSrv = getSuperTypeService();
		if(superTpeSrv != null) {
			Element superTypeEl = superTpeSrv.getUMLElement();
			if ((superTypeEl instanceof Classifier)) {
				Classifier elGeneral = (Classifier) superTypeEl;
				getUMLElement().createGeneralization(elGeneral);
			}
		}
		List<IInterfaceService<?, ?>> superIntServices = 
				getSuperInterfaceServices();
		if(
				(superIntServices != null) 
				&& (superIntServices.size() > 0)
		) {
			for (IInterfaceService<?, ?> superInterface : superIntServices) {
				Classifier interEl = superInterface.getUMLElement();
				Element contEl = getContainerService().getUMLElement();
				if (contEl instanceof Package) {
					Package pack = (Package) contEl;
					Element realize = 
							pack.createPackagedElement(null,
							UMLPackage.eINSTANCE.getRealization());
					if (realize instanceof Realization) {
						Realization real = (Realization) realize;
						NamedElement childEl = getUMLElement();
						real.getClients().add(childEl);
						real.getSuppliers().add(interEl);
					}
				}
			}
		}
	}
	
	// @Override
	public String getSimpleName() {
		String name = null;
		if ((syntaxTreeNode != null) && (syntaxTreeNode.getName() != null)) {
			name = syntaxTreeNode.getName().toString();
		} else if (javaElement != null) {
			name = javaElement.getElementName();
		} else if (defaultSimpleName != null) {
			name = defaultSimpleName;
		}
		return name;
	}

	// @Override
	public IMethodService getOperationService(String n) {
		if (n != null) {
			List<IMethodService> operations = getOperationsServices();
			for (int i = 0; i < operations.size(); i++) {
				if (operations.get(i).getFullName().equals(n))
					return operations.get(i);
			}
		}
		return null;
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

		// @Override
		public ASTRewrite getRewriter() {
			return this.rewriter;
		}
		
	// @Override
	public List<IElementService> getElementsHandlers() {
		List<IElementService> ret = new ArrayList<IElementService>();
		if(properties!=null) {
			IAttributeService prop = null;
			for(int i=0 ; i<properties.size() ; i++) {
				prop = properties.get(i);
				if(prop!=null) {
					ret.add(prop);
				}
			}
		}
		if (operationsServices != null) {
			IMethodService op = null;
			for (int i = 0; i < operationsServices.size(); i++) {
				op = operationsServices.get(i);
				if (op != null) {
					ret.add(op);
				}
			}
		}
		if(types != null) {
			ITypeService<?, ?> cl = null;
			for(int i = 0 ; i < types.size() ; i++) {
				cl = types.get(i);
				if(cl != null) {
					ret.add(cl);
				}
			}
		}
		return ret;
	}
	
	// @Override
	public List<IInterfaceService<?, ?>> getSuperInterfaceServices() {
		if(superInterfacesServices == null) {
			resolveInterfaces();
		}
		return superInterfacesServices;
	}
	
	private void resolveInterfaces() {
		// Super interfaces
		superInterfacesServices = new ArrayList<IInterfaceService<?, ?>>();
		List<String> superInterfacesNames = new ArrayList<String>();
		// AST Node
		if (syntaxTreeNode != null) {
			// We get all the methods declarations
			MethodDeclaration[] operations = syntaxTreeNode.getMethods();
			// We add a handler to the list for each method of the class
			for (MethodDeclaration operation : operations) {
				addOperationService(new OperationService(operation, this));
			}
			List<?> interfaces = syntaxTreeNode.superInterfaceTypes();
			// If this class has super interfaces,
			if (interfaces != null) {
				// Then we try to resolves the binding for each interface,
				Object ob = null;
				org.eclipse.jdt.core.dom.Type tp = null;
				ITypeBinding binding = null;
				for (int i = 0; i < interfaces.size(); i++) {
					ob = interfaces.get(i);
					if ((ob != null)
							&& (ob instanceof org.eclipse.jdt.core.dom.Type)) {
						tp = (org.eclipse.jdt.core.dom.Type) ob;
						binding = tp.resolveBinding();
						if (binding != null) {
							// Aiming to get a qualified name
							superInterfacesNames
									.add(binding.getQualifiedName());
						}
					}
				}
			}
			// Java element
		} else if (javaElement != null) {
			try {
				IMethod[] operations = javaElement.getMethods();
				if (operations != null) {
					// We add a handler to the list for each method of the
					// class
					for (int i = 0; i < operations.length; i++) {
						addOperationService(new OperationService(operations[i],
								this));
					}
				}
				String[] superIntNames = javaElement.getSuperInterfaceNames();
				if (superIntNames != null) {
					for (int i = 0; i < superIntNames.length; i++) {
						String[][] parts = javaElement
								.resolveType(superIntNames[i]);
						String name = nameReconstruction(parts);
						if (name != null) {
							superInterfacesNames.add(name);
						}
					}
				}
				for (String intName : superInterfacesNames) {
					ITypeService<?, ?> tSrv = getModelService()
							.resolveTypeService(intName);
					if (tSrv instanceof IInterfaceService) {
						IInterfaceService<?, ?> srv = (IInterfaceService<?, ?>) tSrv;
						superInterfacesServices.add(srv);
					}
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setUpUMLModelElement() {
		if(umlModelElement == null)loadExistingUmlElement();
		if(umlModelElement == null) {
			createUmlElement();
			setupSuperTypeUMLModelElement();	
		}
		for(IAttributeService propSrv : properties) {
			propSrv.setUpUMLModelElement();
		}
		for(IMethodService opSrv : operationsServices) {
			if(opSrv != null)opSrv.setUpUMLModelElement();
		}
		for(IAssociationService<?, ?> assSrv : dependenciesServices) {
			assSrv.setUpUMLModelElement();
		}
		for(ITypeService<?, ?> typeSrv : types) {
			typeSrv.setUpUMLModelElement();
		}
		if(noteService != null)noteService.setUpUMLModelElement();
	}
	
	// @Override
	@SuppressWarnings("unchecked")
	protected void loadExistingUmlElement() {
		ITypesContainerService cont = getContainerService();
		Element contEl = cont.getUMLElement();
		if(contEl instanceof Namespace) {
			Namespace ns = (Namespace)contEl;
			NamedElement el = ns.getMember(getSimpleName());
			if(el instanceof Classifier) {
				umlModelElement = (E)el;
				VisibilityKind vis = getVisibility();
				if(!(umlModelElement.getVisibility() == vis)) {
					umlModelElement.setVisibility(vis);
				}
				boolean abst = isAbstract();
				if(!umlModelElement.isAbstract() == abst) {
					umlModelElement.setIsAbstract(abst);
				}
			}
		}
	}
	
	/** Property removal runnable */
	public class PropertyRemoval 
	extends AbstractUMLToCodeModificationRunnable
	<Property, IAttributeService> {

		/** Uninitialized property removal construction */
		public PropertyRemoval() {
		}

		/**
		 * Property removal construction
		 * @param p
		 * Value of {@link #objective}
		 */
		public PropertyRemoval(Property p) {
			this();
			objective = p;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (objective != null) {
				String simpleName = 
					PropertyService.codeSimpleNameExtraction(objective);
				String containerName = getFullName();
				String qualifiedName = containerName + '#' + simpleName;
				/*
				String qualifiedName = 
					PropertyHandler.resolveFullyQualifiedName(oldProperty);
				 */
				IAttributeService tp = getPropertyService(qualifiedName);
				if (tp != null) {
					IField jEl = tp.getJavaElement();
					if (jEl != null) {
						try {
							jEl.delete(false, new NullProgressMonitor());
							properties.remove(tp);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/** Property creation runnable */
	public class PropertyCreation 
	extends AbstractUMLToCodeModificationRunnable
	<Property, IAttributeService> {

		/** Property owner handler */
		private IClassifierService<?, ?> ownerHandler;

		/** Uninitialized property creation */
		public PropertyCreation() {
		}

		/**
		 * Property creation
		 * @param p
		 * Value of {@link #objective}
		 * @param o
		 * Value of {@link #ownerHandler}
		 */
		public PropertyCreation(Property p, IClassifierService<?, ?> o) {
			this();
			objective = p;
			ownerHandler = o;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			IAttributeService prop = null;
			if(objective!=null) {
				String simpleName = objective.getName();
				if(
						(simpleName!=null)
						&&(simpleName.length()>0)
				) {
					try {
						String content = new String();
						String typeName = new String();
						Type t = objective.getType();
						if(t!=null) {
							typeName += t.getName();
						}
						else {
							typeName += 
								Resources.getCodeConstant(
										"constants.defaultType"
								);
						}
						String visibility = new String();
						VisibilityKind v = objective.getVisibility();
						if(v!=null) {
							switch (v.getValue()) {
							case VisibilityKind.PRIVATE:
								visibility = 
									Resources.getCodeConstant(
											"constants.privateVisibility"
									);
								break;
							case VisibilityKind.PACKAGE:
								visibility = 
									Resources.getCodeConstant(
											"constants.packageVisibility"
									);
								break;
							case VisibilityKind.PROTECTED:
								visibility = 
									Resources.getCodeConstant(
											"constants.protectedVisibility"
									);
								break;
							case VisibilityKind.PUBLIC:
								visibility = 
									Resources.getCodeConstant(
											"constants.publicVisibility"
									);
								break;
							default:
								break;
							};
						}
						content += 
							Resources.getCodeConstant("constants.newLine");
						if(visibility!=null) {
							content += visibility + ' ';
						}
						content += 
							typeName 
							+ ' '
							+ simpleName 
							+ Resources.getCodeConstant(
									"constants.endStatement"
							);
						IField f = 
							javaElement.createField(
									content, 
									null, 
									false, 
									new NullProgressMonitor()
							);
						prop = new PropertyService(f, ownerHandler);
						properties.add(prop);
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
			result = prop;
		}
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
}