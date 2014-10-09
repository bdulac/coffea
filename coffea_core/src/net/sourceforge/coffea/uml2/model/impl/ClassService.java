package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IAttributeService;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.ITypesContainerService;
import net.sourceforge.coffea.uml2.model.impl.CompositionService;
import net.sourceforge.coffea.uml2.model.impl.PropertyService;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/** Service for a class */
public class ClassService 
extends InterfaceService 
implements IClassService<TypeDeclaration, IType> {	

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -7998749245698223200L;

	/** Super class name */
	protected String superClassName;

	/** Super type handler */
	protected ITypeService<?, ?> superType;

	/** List of properties belonging to the handled class */
	protected List<IAttributeService> properties;

	/** List of nested classes belonging to the handled class */
	protected List<ITypeService<?, ?>> types;

	/**
	 * Class service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	public ClassService(ITypesContainerService p, String nm) {
		super(p, nm);
		this.completeClassConstruction(null, p);
	}

	/**
	 * Class service construction without any declaration but with an existing 
	 * UML element
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	public ClassService(
			ITypesContainerService p, 
			String nm, 
			Class c
	) {
		super(p, nm, c);
		this.completeClassConstruction(null, p);
	}

	/**
	 * Class service construction from an AST node
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 * @param r
	 * Value of {@link #rewriter}
	 * @param u
	 * Value of {@link #parsedUnit}
	 */
	public ClassService(
			TypeDeclaration stxNode, 
			ITypesContainerService p, 
			ASTRewrite r, 
			CompilationUnit u
	) {
		super(stxNode, p, r, u);
		this.completeClassConstruction(r, p, u);
	}

	/**
	 * Class service construction from an AST node and an existing UML element
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 * @param u
	 * Value of {@link #parsedUnit}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	public ClassService(
			TypeDeclaration stxNode, 
			ITypesContainerService p, 
			CompilationUnit u, 
			Class c
	) {
		super(stxNode, p, u, c);
		this.completeClassConstruction(null, p, u);
	}

	/**
	 * Class service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param u
	 * Value of {@link #processedUnit}
	 */
	public ClassService(
			IType jEl, 
			ITypesContainerService p, 
			ICompilationUnit u
	) {
		super(jEl, p, u);
		this.completeClassConstruction(null, p, u);
	}

	/**
	 * Class service construction from a Java element and an existing UML 
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
	public ClassService(
			IType jEl, 
			ITypesContainerService p, 
			ASTRewrite r, 
			ICompilationUnit u, 
			Class c
	) {
		super(jEl, p, r, u, c);
		this.completeClassConstruction(r, p, u);
	}

	//Completes the constructors, factorization of the specialized part
	protected void completeClassConstruction(
			ASTRewrite r, 
			ITypesContainerService p
	) {
		superInterfaces = new ArrayList<IInterfaceService<?, ?>>();
		superInterfacesNames = new ArrayList<String>();
		//super.completeConstruction(r, p);
		//p.addClassHandler(this);
		this.rewriter = r;
		this.types = new ArrayList<ITypeService<?, ?>>();
		//We build a list of properties tools
		this.properties = new ArrayList<IAttributeService>();
		// We build a list of dependencies tools
		this.dependenciesServices = new ArrayList<CompositionService>();
		if(syntaxTreeNode!=null) {
			// We get all the attribute declarations
			FieldDeclaration[] fields = syntaxTreeNode.getFields();
			// For each attribute of the class,
			for (int i=0 ; i<fields.length ; i++) {
				// We add a tool to the list 
				addPropertyService(new PropertyService(fields[i], this));
				// And a dependency
				dependenciesServices.add(
						new CompositionService(fields[i], this)
				);
			}
			// If this class has a super class,
			if(syntaxTreeNode.getSuperclassType()!=null) {
				// We resolve this super class
				ITypeBinding binding = 
					syntaxTreeNode.getSuperclassType().resolveBinding();
				// And try to get its name
				if(binding!=null) {
					superClassName = binding.getQualifiedName();
					int smtIdx = superClassName.indexOf('<');
					if(smtIdx>=0) {
						superClassName = 
							superClassName.substring(0, smtIdx);
					}
				}
			}
		}
		else if(javaElement!=null) {
			// We get all the fields
			try {
				IField[] fields = javaElement.getFields();
				// For each field of the class,
				for (int i=0 ; i<fields.length ; i++) {
					// We add a tool to the list 
					addPropertyService(
							new PropertyService(fields[i], this)
					);
					// And a dependency
					dependenciesServices.add(
							new CompositionService(fields[i], this)
					);
				}
				// If this class has a super class,
				if(javaElement.getSuperclassName()!=null) {
					// The we note its name
					String superSimpleName = javaElement.getSuperclassName();
					String[][] namesParts = 
						javaElement.resolveType(superSimpleName);
					String name = nameReconstruction(namesParts);
					if(name!=null) {
						superClassName = name;
					}
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}
		}
	}

	protected void completeClassConstruction(
			ASTRewrite r, 
			ITypesContainerService p, 
			ICompilationUnit c
	) {
		processedUnit = c;
		this.completeClassConstruction(r, p);
	}

	protected void completeClassConstruction(
			ASTRewrite r, 
			ITypesContainerService p, 
			CompilationUnit c
	) {
		parsedUnit = c;
		this.completeClassConstruction(r, p);
	}

	/*
	@Override
	public String getFullName() {
		String name = new String();
		buildFullyQualifiedName(javaElement);
		if(getContainerHandler() instanceof IClassHandling<?, ?>) {	
			name += getContainerHandler().getFullName() + '$';
			String simpleName = getSimpleName();
			//Removing  anything before any point in the simple name for 
			//the hierarchy transition case
			int ind = -1;
			if(
					((ind = simpleName.indexOf('.'))!=-1)
					&&(ind<simpleName.length())
			) {
				simpleName = simpleName.substring(ind +1);
			}
			name += simpleName;

		}
		else name = super.getFullName();
		return name;
	}
	 */

	@Override
	public void setUpUMLModelElement() {
		if(umlModelElement==null) {
			// FIXME Problem of double setup : by the class itself and as a 
			// type
			super.setUpUMLModelElement();
			for(int i=0 ; i<types.size() ; i++) {
				types.get(i).setUpUMLModelElement();
			}
			for(int i=0 ; i<properties.size() ; i++) {
				properties.get(i).setUpUMLModelElement();
			}
			for(int i=0 ; i<dependenciesServices.size() ; i++) {
				dependenciesServices.get(i).setUpUMLModelElement();
			}
		}
	}

	@Override
	protected void setupSuperTypeUMLModelElement() {
		super.setupSuperTypeUMLModelElement();
		if(superClassName!=null) {
			superType = getModelService().resolveTypeService(superClassName);
			if(
					(superType!=null)
					&&(superType.getUMLElement() instanceof Classifier)
			) {
				Classifier elGeneral = (Classifier)superType.getUMLElement();
				getUMLElement().createGeneralization(
						elGeneral
				);
			}	
		}
	}

	public void addPropertyService(IAttributeService prH) {
		properties.add(prH);
	}

	public List<IAttributeService> getPropertiesServices() {
		return properties;
	}

	public List<ITypeService<?, ?>> getTypesServices() {
		return types;
	}

	public void addTypeService(ITypeService<?, ?> clH) {
		types.add(clH);	
	}

	public ITypeService<?, ?> resolveTypeService(String n) {
		if(n!=null) {
			if(n.equals(this.getFullName()))return this;
			List<ITypeService<?, ?>> cls = getTypesServices();
			for(int i=0 ; i<cls.size() ; i++) {
				if(cls.get(i).getFullName().equals(n))
					return cls.get(i);
			}
		}
		return null;
	}

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

	@Override
	public IElementService getElementService(String n) {
		IElementService ret = null;
		if(n!=null) {
			if((ret==null)&&(types!=null)) {
				ITypeService<?, ?> cl;
				for(int i=0 ; i<types.size() ; i++) {
					cl = types.get(i);
					if(cl!=null) {
						if(n.equals(cl.getFullName())) {
							ret = cl;
						}
						else {
							ret = cl.getElementService(n);
						}
						if(ret!=null) {
							break;
						}
					}
				}
			}
			if((ret==null)&&(properties!=null)) {
				IAttributeService prop;
				for(int i=0 ; i<properties.size() ; i++) {
					prop = properties.get(i);
					if(prop!=null) {
						if(n.equals(prop.getFullName())) {
							ret = prop;
						}
						if(ret!=null) {
							break;
						}
					}
				}
			}
			if((ret==null)&&(operationsServices!=null)) {
				IMethodService op;
				for(int i=0 ; i<operationsServices.size() ; i++) {
					op = operationsServices.get(i);
					if(op!=null) {
						if(n.equals(op.getFullName())) {
							ret = op;
						}
						if(ret!=null) {
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	@Override
	public List<IElementService> getElementsHandlers() {
		List<IElementService> ret = super.getElementsHandlers();
		if(properties!=null) {
			IAttributeService prop = null;
			for(int i=0 ; i<properties.size() ; i++) {
				prop = properties.get(i);
				if(prop!=null) {
					ret.add(prop);
				}
			}
		}
		if(types!=null) {
			ITypeService<?, ?> cl = null;
			for(int i=0 ; i<types.size() ; i++) {
				cl = types.get(i);
				if(cl!=null) {
					ret.add(cl);
				}
			}
		}
		return ret;
	}

	/*
	@Override
	public void acceptModelChangeNotification(Notification nt) {
		super.acceptModelChangeNotification(nt);
		if(nt!=null) {
			// Object feature = nt.getFeature();
			Object newValue = nt.getNewValue();
			Object oldValue = nt.getOldValue();
			Class newClass = null;
			Class oldClass = null;
			Property newProperty = null;
			Property oldProperty = null;
			if (newValue instanceof Class) {
				newClass = (Class) newValue;
			}
			else if (newValue instanceof Property) {
				newProperty = (Property)newValue;
			}
			if(oldValue instanceof Class) {
				oldClass = (Class)oldValue;
			}
			else if (oldValue instanceof Property) {
				oldProperty = (Property)oldValue;
			}
			int type = nt.getEventType();
			switch (type) {
			case Notification.ADD:
				if (newProperty != null) {
					createProperty(newProperty);
				}
				if( newClass != null) {
					createNestedClass(newClass);
				}
				break;
			case Notification.ADD_MANY:
				break;
			case Notification.EVENT_TYPE_COUNT:
				break;
			case Notification.MOVE:
				break;
			case Notification.NO_FEATURE_ID:
				break;
			case Notification.REMOVE:
				if((newClass == null) && (oldClass != null)) {
					removeNestedClass(oldClass);
				}
				else if ((newProperty==null) && (oldProperty != null)) {
					removeProperty(oldProperty);
				}
			case Notification.REMOVE_MANY:
				break;
			case Notification.REMOVING_ADAPTER:
				break;
			case Notification.RESOLVE:
				break;
			case Notification.SET:
				break;
			case Notification.UNSET:
				break;
			default:
				break;
			}
		}
	}
	 */

	public IClassService<?, ?> createNestedClass(Class newClass) {
		IClassService<?, ?> cl = null;
		if(newClass!=null) {
			ClassCreation runnable = new ClassCreation(newClass, this);
			CoffeaUML2Plugin.getInstance().execute(runnable);
			cl  = runnable.getResult();
		}
		return cl;
	}

	public void deleteNestedClass(Class oldClass) {
		if(oldClass!=null) {
			ClassRemoval runnable = new ClassRemoval(oldClass, this);
			CoffeaUML2Plugin.getInstance().execute(runnable);
		}
	}

	public IAttributeService createProperty(Property p) {
		IAttributeService a = null;
		if(p!=null) {
			PropertyCreation runnable = new PropertyCreation(p, this);
			CoffeaUML2Plugin.getInstance().execute(runnable);
			a  = runnable.getResult();
		}
		return a;
	}

	public void deleteProperty(Property p) {
		if(p!=null) {
			PropertyRemoval runnable = new PropertyRemoval(p);
			CoffeaUML2Plugin.getInstance().execute(runnable);
		}
	}

	/** Nested class creation runnable */
	public class ClassCreation 
	extends AbstractUMLToCodeModificationRunnable
	<Class, IClassService<?, ?>> {

		/** Nesting class handler */
		private IClassService<?, ?> nestingClassHandler;

		/** Uninitialized class creation */
		public ClassCreation() {
		}

		/**
		 * Nested class creation
		 * @param c
		 * Value of {@link #objective}
		 * @param n
		 * Value of {@link #nestingClassHandler}
		 */
		public ClassCreation(Class c, IClassService<?, ?> n) {
			this();
			objective = c;
			nestingClassHandler = n;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			if(objective!=null) {
				String simpleName = 
					ClassService.simpleNameExtraction(objective);
				if(
						(simpleName!=null)
						&&(simpleName.length()>0)
				) {
					try {
						String content = new String();
						content += 
							Resources.getCodeConstant("constants.newLine") 
							+ 
							Resources.getCodeConstant(
									"constants.publicVisibility"
							)
							+ ' '
							+ 
							Resources.getCodeConstant("constants.class") 
							+ ' '
							+ simpleName 
							+ ' '
							+ Resources.getCodeConstant("constants.openBlock");
						content += 
							Resources.getCodeConstant("constants.newLine") 
							+ 
							Resources.getCodeConstant(
									"constants.closeBlock"
							);
						IType newType = 
							javaElement.createType(
									content, 
									null, 
									true, 
									new NullProgressMonitor()
							);
						result = 
							new ClassService(
									newType, 
									nestingClassHandler, 
									null
							);
						types.add(result);
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/** Nested class removal runnable */
	public class ClassRemoval 
	extends AbstractUMLToCodeModificationRunnable
	<Class, IClassService<?, ?>> {

		/** Nesting class handler */
		private IClassService<?, ?> nestingClassHandler;

		/** Uninitialized class creation */
		public ClassRemoval() {
		}

		/**
		 * Nested class creation
		 * @param c
		 * Value of {@link #objective}
		 * @param n
		 * Value of {@link #nestingClassHandler}
		 */
		public ClassRemoval(Class c, IClassService<?, ?> n) {
			this();
			objective = c;
			nestingClassHandler = n;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			if(objective!=null) {
				String simpleName = 
					ClassService.simpleNameExtraction(objective);
				String qualifiedName = getFullName() + '$' + simpleName;
				ITypeService<?, ?> tpH = resolveTypeService(qualifiedName);
				if(tpH!=null) {
					IType jEl = tpH.getJavaElement();
					if(jEl!=null) {
						try {
							jEl.delete(
									false, 
									new NullProgressMonitor()
							);
							nestingClassHandler.getTypesServices().remove(tpH);
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
		private IClassService<?, ?> ownerHandler;

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
		public PropertyCreation(Property p, IClassService<?, ?> o) {
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
}