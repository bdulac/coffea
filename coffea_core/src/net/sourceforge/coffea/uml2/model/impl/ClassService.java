package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IGroupService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.ITypesContainerService;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/** Service for a class */
public class ClassService 
extends ClassifierService<Class, TypeDeclaration, IType> 
implements IClassService<TypeDeclaration, IType> {	

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -7998749245698223200L;

	/**
	 * Class service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	public ClassService(ITypesContainerService p, String nm) {
		super(p, nm);
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
		super(stxNode, r, p, u);
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
		// completeTypeConstruction(null, p, u);
	}

	@Override
	public void createUmlElement() {
		String name = null;
		if((syntaxTreeNode != null)&&(syntaxTreeNode.getName() != null)) {
			name = syntaxTreeNode.getName().getFullyQualifiedName();
		}
		else if(javaElement != null) {
			// name = javaElement.getTypeQualifiedName();
			name = javaElement.getElementName();
		}
		else if(defaultSimpleName != null) {
			name = getSimpleName();
		}
		IGroupService parent = getContainerService();
		Element parentEl = parent.getUMLElement();
		if(parentEl instanceof org.eclipse.uml2.uml.Package) {
			org.eclipse.uml2.uml.Package pack = 
					(org.eclipse.uml2.uml.Package)parentEl;
			umlModelElement = pack.createOwnedClass(name, isAbstract());
		}
		else if(parentEl instanceof Class) {
			Class nestingClassEl = (Class)parentEl;
			umlModelElement = UMLFactory.eINSTANCE.createClass();
			int indDollar = -1;
			if ((indDollar = name.indexOf('$')) >= 0) {
				name = name.substring(indDollar + 1);
			}
			umlModelElement.setIsAbstract(isAbstract());
			umlModelElement.setName(name);
			nestingClassEl.getNestedClassifiers().add(umlModelElement);
		}
		umlModelElement.setVisibility(getVisibility());
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

	// @Override
	public IClassService<?, ?> createNestedClass(Class newClass) {
		IClassService<?, ?> cl = null;
		if(newClass!=null) {
			ClassCreation runnable = new ClassCreation(newClass, this);
			CoffeaUML2Plugin.getInstance().execute(runnable);
			cl  = runnable.getResult();
		}
		return cl;
	}

	// @Override
	public void deleteNestedClass(Class oldClass) {
		if(oldClass!=null) {
			ClassRemoval runnable = new ClassRemoval(oldClass, this);
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

		// @Override
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
						addTypeService(result);
						// types.add(result);
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

		// @Override
		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			if(objective != null) {
				String simpleName = 
					ClassService.simpleNameExtraction(objective);
				String qualifiedName = getFullName() + '$' + simpleName;
				ITypeService<?, ?> tSrv = resolveTypeService(qualifiedName);
				if(tSrv != null) {
					IType jEl = tSrv.getJavaElement();
					if(jEl != null) {
						try {
							jEl.delete(
									false, 
									new NullProgressMonitor()
							);
							nestingClassHandler.getTypesServices().remove(tSrv);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}