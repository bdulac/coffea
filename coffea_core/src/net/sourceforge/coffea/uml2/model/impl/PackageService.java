package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IContainerService;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IGroupService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.IPackagesGroupService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.creation.IJavaElementServiceBuilding;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename
.RenamePackageProcessor;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.VisibilityKind;

/** Service for a package */
public class PackageService 
extends ASTNodeService<Package, PackageDeclaration, IPackageFragment> 
implements IPackageService {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -2201865524904227312L;

	/**
	 * Builds an UML package fully qualified name
	 * @param resolvPack
	 * UML Package to build the name from
	 * @return UML Package fully qualified name
	 */
	public static String buildFullyQualifiedName(Package resolvPack) {
		String name = new String();
		Package pack = resolvPack.getNestingPackage();
		while (pack != null) {
			if (
					(pack instanceof Model)
					&&(pack.getName()!=null)
					&&(pack.getName().length() > 0)
					&&(
							!pack.getName().equals(
									IModelService.defaultPackageFileName
							)
					)
			) {
				name = pack.getName() + '.' + name;
				pack = null;
			} else if(
					(pack.getName()!=null)
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
		name += resolvPack.getName();
		return name;
	}

	/**
	 * Builds an AST package declaration fully qualified name
	 * @param node
	 * Package declaration to build the full name from
	 * @return AST package declaration fully qualified name
	 */
	public static String buildFullyQualifiedName(PackageDeclaration node) {
		String fullName = null;
		if (node != null) {
			return node.getName().getFullyQualifiedName();
		}
		return fullName;
	}

	/**
	 * Builds a Java package fragment fully qualified name
	 * @param node
	 * Package fragment to build the full name from
	 * @return Package fragment fully qualified name
	 */
	public static String buildFullyQualifiedName(IPackageFragment element) {
		String fullName = null;
		if (element != null) {
			fullName = element.getElementName();
			if((fullName==null)||(fullName.length()==0)) {
				fullName = IModelService.defaultPackageFileName;
			}
		}
		return fullName;
	}

	/**
	 * Java type simple name extraction from the corresponding UML element
	 * @param p
	 * UML element from which the name will be extracted
	 * @return Extracted simple name
	 */
	public static String simpleNameExtraction(Package p) {
		String simpleName = null;
		if(p!=null) {
			simpleName = p.getName();
		}
		return simpleName;
	}

	/** List of handlers for sub-packages of the current package */
	protected List<IPackageService> packages;

	/** List of handlers for types contained in the current package */
	protected List<ITypeService<?, ?>> types;

	/**
	 * Construction of a package handler without an existing declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	public PackageService(IPackagesGroupService p, String nm) {
		super(p, nm);
		completeConstruction(p);
	}

	/**
	 * Construction of a package handler without an existing declaration but 
	 * with an existing UML element
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 * @param pk
	 * Value of {@link #umlModelElement}
	 */
	public PackageService(IPackagesGroupService p, String nm, Package pk) {
		super(p, nm, pk);
		completeConstruction(p);
	}

	/**
	 * Construction of a package handler
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 */
	public PackageService(
			PackageDeclaration stxNode, 
			IPackagesGroupService p
	) {
		super(stxNode, p);
		completeConstruction(p);
	}

	/**
	 * Construction of a package service from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 */
	public PackageService(IPackageFragment jEl, IPackagesGroupService p) {
		super(jEl, p);
		completeConstruction(p);
	}

	/**
	 * Construction of a package service from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param pk
	 * Value of {@link #umlModelElement}
	 */
	public PackageService(
			IPackageFragment jEl, 
			IPackagesGroupService p,
			Package pk) {
		super(jEl, p, pk);
		completeConstruction(p);
	}

	public void retrieveContainerFromHierarchy() {
		IModelService m = getModelService();
		IPackagesGroupService parent = null;
		String parentName = getFullName();
		int indexFirstPoint = -1;
		while (parent == null) {
			indexFirstPoint = parentName.lastIndexOf('.');
			if (indexFirstPoint >= 0) {
				parentName = parentName.substring(0, indexFirstPoint);
				parent = m.resolvePackageService(parentName);
			} else {
				// We try to get a default package if this is not the default
				// package
				String fullName = getFullName();
				if(!
						(
								(fullName!=null)
								&&(
										fullName.equals(
												IModelService
												.defaultPackageFileName
										)
								)
						)
				) {
					parent = 
						m.resolvePackageService(
								IModelService.defaultPackageFileName
						);
				}
				// If we have no parent at this point, 
				if(parent==null) {
					// Then this will be the model
					parent = m;
				}
			}
		}
		container = parent;
	}

	public List<IPackageService> fetchSubPackagesFromHierarchy() {
		List<IPackageService> children = new ArrayList<IPackageService>();
		IModelService m = getModelService();
		if (m != null) {
			List<IPackageService> allPackages = m.getPackagesServices();
			if (allPackages != null) {
				IPackageService p = null;
				for (int i = 0; i < allPackages.size(); i++) {
					p = allPackages.get(i);
					if (p != null) {
						if ((p.getContainerService().equals(this))) {
							p.fetchSubPackagesFromHierarchy();
							children.add(p);
							addPackageService(p);
						}
					}
				}
			}
		}
		return children;
	}

	// Completes the constructors, factorization of the specialized part
	private void completeConstruction(IPackagesGroupService p) {
		p.addPackageService(this);
		this.packages = new ArrayList<IPackageService>();
		this.types = new ArrayList<ITypeService<?, ?>>();
	}

	public IModelService getModelService() {
		if (this instanceof IModelService) {
			return (IModelService) this;
		} else {
			return getContainerService().getModelService();
		}
	}
	
	@Override
	protected void loadExistingUmlElement() {
		// TODO Auto-generated method stub
		IGroupService parent = getContainerService();
		Package parentElement = null;
		if (parent instanceof IPackageService) {
			IPackageService pk = (IPackageService) parent;
			parentElement = pk.getUMLElement();
		} else if (parent instanceof IModelService) {
			IModelService md = (IModelService) parent;
			parentElement = md.getUMLElement();
		}
		if(parentElement != null) {
			Element el = 
					parentElement.getPackagedElement(getSimpleName());
			if(el instanceof Package)umlModelElement = (Package)el;
		}
	}
	
	@Override
	protected void createUmlElement() {
		
		IGroupService parent = getContainerService();
		Package parentElement = null;
		if (parent instanceof IPackageService) {
			IPackageService pk = (IPackageService) parent;
			parentElement = pk.getUMLElement();
		} else if (parent instanceof IModelService) {
			IModelService md = (IModelService) parent;
			parentElement = md.getUMLElement();
		}
		// If the parent element is a package,
		if (
				(parentElement != null) 
				&& (parent instanceof IPackageService)
				&& (umlModelElement == null)
		) {
			// Then we create a nested package
			umlModelElement = 
				parentElement.createNestedPackage(getSimpleName());
		} else if (
				(parent instanceof IModelService)
				&&(parentElement != null)
		) {
			/*
			 * Else, if the parent is the model, 
			 * If we have the primitive types package or the class path
			 */
			if(
					(getSimpleName().equals("Primitive types"))
					||(getSimpleName().equals("classpath"))
			) {
				// Then we create a dedicated package
				umlModelElement = 
					parentElement.createNestedPackage(getSimpleName());
				umlModelElement.setVisibility(
						VisibilityKind.PRIVATE_LITERAL
				);
			}
			else {
				/*
				// If the selection is the project, 
				if(((md==parent)&&(md.isSelectionEmptyProject()))) {
					umlModelElement = 
						parentElement.createNestedPackage(getSimpleName());
				}
				else {
				 */
				/*
				 * Else if the selection was not the project, we have 
				 * the root package, the UML element for this package 
				 * will be the model itself
				 */
				umlModelElement = parentElement;
				// }
			}
		}
	}

	@Override
	public void setUpUMLModelElement() {
		boolean init = false;
		if(umlModelElement == null) {
			init = true;
			loadExistingUmlElement();
		}
		if (umlModelElement == null) {
			createUmlElement();
		}
		if(init) {
			for (int i = 0; i < packages.size(); i++) {
				packages.get(i).setUpUMLModelElement();
			}
			for (int i = 0; i < types.size(); i++) {
				types.get(i).setUpUMLModelElement();
			}
		}
		if(noteService != null)noteService.setUpUMLModelElement();
	}

	public IPackageService resolvePackageService(String n) {
		if (this.getFullName().equals(n)) {
			return this;
		}
		for (int i = 0; i < this.packages.size(); i++) {
			IPackageService child;
			if (
					(child = this.packages.get(i).resolvePackageService(n)) 
					!= null
			) {
				return child;
			}
		}
		return null;
	}

	public ITypeService<?, ?> resolveTypeService(String n) {
		if (n != null) {
			ITypeService<?, ?> t;
			for (int i = 0; i < types.size(); i++) {
				t = types.get(i);
				if ((t != null) && (t.getFullName().equals(n))) {
					return t;
				}
				else if((t != null) && (t instanceof IClassService<?, ?>)) {
					IClassService<?, ?> clH = (IClassService<?, ?>)t;
					t = clH.resolveTypeService(n);
					if(t != null) {
						return t;
					}
				}
			}
			// No class found directly in the package, trying to get it from
			// sub packages
			for (int i = 0; i < packages.size(); i++) {
				t = packages.get(i).resolveTypeService(n);
				if (t != null)
					return t;
			}
		}
		return null;
	}

	public IElementService getElementService(String n) {
		IElementService ret = null;
		if (n != null) {
			if ((ret == null) && (types != null)) {
				ITypeService<?, ?> t;
				for (int i = 0; i < types.size(); i++) {
					t = types.get(i);
					if (t != null) {
						String fName = t.getFullName();
						if (n.equals(fName)) {
							ret = t;
						} else {
							ret = t.getElementService(n);
						}
						if (ret != null) {
							break;
						}
					}
				}
			}
			if ((ret == null) && (packages != null)) {
				IPackageService p;
				for (int i = 0; i < packages.size(); i++) {
					p = packages.get(i);
					if (p != null) {
						String fName = p.getFullName();
						if (n.equals(fName)) {
							ret = p;
						} else {
							ret = p.getElementService(n);
						}
						if (ret != null) {
							break;
						}
					}
				}
			}
		}
		return ret;
	}

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

	public List<IElementService> getElementsHandlers() {
		List<IElementService> ret = new ArrayList<IElementService>();
		if (types != null) {
			ITypeService<?, ?> t;
			for (int i = 0; i < types.size(); i++) {
				t = types.get(i);
				if (t != null) {
					ret.add(t);
				}
			}
		}
		if (packages != null) {
			IPackageService p;
			for (int i = 0; i < packages.size(); i++) {
				p = packages.get(i);
				if (p != null) {
					ret.add(p);
				}
			}
		}
		return ret;
	}

	public List<IPackageService> getPackagesServices() {
		return this.packages;
	}

	public List<ITypeService<?, ?>> getTypesServices() {
		return this.types;
	}

	public void addPackageService(IPackageService el) {
		this.packages.add(el);
		el.setGroupService(this);
	}

	public void addTypeService(ITypeService<?, ?> el) {
		this.types.add(el);
		el.setContainerService(this);
	}

	public IPackagesGroupService getContainerService() {
		return (IPackagesGroupService) container;
	}

	public void setGroupService(IPackagesGroupService gr) {
		container = gr;
	}

	public String getSimpleName() {
		IModelService m = getModelService();
		if ((m != null) && (m.arePackageInHierarchy())) {
			String simpleName = null;
			if (
					(syntaxTreeNode != null) 
					&& (syntaxTreeNode.getName() != null)
			) {
				String fullName = syntaxTreeNode.getName().toString();
				String containerFullName = getContainerService().getFullName();
				if (
						(containerFullName != null) 
						&& (fullName != null)
						&& (!containerFullName.equals(fullName))
				) {
					simpleName = fullName
					.substring(containerFullName.length() + 1);
				} else {
					simpleName = fullName;
				}
			} else if (javaElement != null) {
				if(simpleName==null) {
					simpleName = javaElement.getElementName();
					String containerFullname = container.getFullName();
					if(
							(containerFullname!=null)
							&&(!
									containerFullname.equals(
											ModelService.defaultPackageFileName
									)
							)
					) {
						int indLastPoint = -1;
						if ((simpleName != null)
								&& (
										(indLastPoint 
												= simpleName.lastIndexOf('.')
										) >= 0
								)
								&& (indLastPoint < simpleName.length())) {
							simpleName = simpleName.substring(indLastPoint + 1);
						}
					}
					if((simpleName==null)||(simpleName.length()==0)) {
						simpleName = ModelService.defaultPackageFileName;
					}
				}
			} else if (defaultSimpleName!=null) {
				simpleName  = defaultSimpleName;
			}
			return simpleName;
		} else {
			return getFullName();
		}
	}

	@Override
	public String getFullName() {
		String fullName = null;
		if (syntaxTreeNode != null) {
			fullName = buildFullyQualifiedName(syntaxTreeNode);
		} else if (javaElement != null) {
			fullName = buildFullyQualifiedName(javaElement);
		} else if (defaultSimpleName!=null) {
			if(
					(
							defaultSimpleName.equals(
									IModelService.primitiveTypesPackageName
							)
					)
					||(
							defaultSimpleName.equals(
									IModelService.classpathTypesPackageName
							)
					)
			) {
				fullName = new String();
			}
			else {
				fullName = defaultSimpleName;
			}
		} else {
			fullName = super.getFullName();
		}
		return fullName;
	}

	/*
	public void resourceSetChanged(ResourceSetChangeEvent evt) {
		IModelHandling modelH = getModelHandler();
		if ((evt != null) && (modelH!=null)) {
			List<Notification> notifications = evt.getNotifications();
			if (notifications != null) {
				Notification notification = null;
				for (int i = 0; i < notifications.size(); i++) {
					notification = notifications.get(i);
					Object feature = notification.getFeature();
					Object newValue = notification.getNewValue();
					Object oldValue = notification.getOldValue();
					if(notification!=null) {
						Element el = null;
						if(notification.getNotifier() instanceof Element) {
							el = (Element)notification.getNotifier();
						}
						else if(oldValue instanceof Element) {
							el = (Element)oldValue;
						}
						if(el!=null) {
							IElementHandling elH = 
								modelH.getElementHandler(el);
							if(
									(elH==null)
									&&(feature instanceof EAttribute)
							) {
								EAttribute attr = (EAttribute) feature;
								String attrName = attr.getName();
								if(
										(oldValue instanceof String)
										&&(newValue instanceof String)
										&&(attrName.equals(NAME_ATTRIBUTE))
								) {
									String fullyQualifiedNewName = 
										ElementHandler
										.buildFullyQualifiedName(el);
									if(fullyQualifiedNewName!=null) {
										String fullyQualifiedOldName = 
											fullyQualifiedNewName.replace(
													(String)newValue, 
													(String)oldValue
											);
										elH = 
											modelH.getElementHandler(
													fullyQualifiedOldName
											);
									}
								}
							}
							if(elH!=null) {
								elH.acceptModelChangeNotification(
										notification
								);
							}

						}
						int type = notification.getEventType();
						switch (type) {
						case Notification.ADD:
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
							break;
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
			}
		}
	}
	 */

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
			Package newPackage = null;
			Package oldPackage = null;
			if (newValue instanceof Class) {
				newClass = (Class) newValue;
			}
			if (oldValue instanceof Class) {
				oldClass = (Class) oldValue;
			}
			if (newValue instanceof Package) {
				newPackage = (Package)newValue;
			}
			if (oldValue instanceof Package) {
				oldPackage = (Package)oldValue;
			}
			int type = nt.getEventType();
			switch (type) {
			case Notification.ADD:				
				if( newClass != null) {
					createClass(newClass);
				}
				if( newPackage != null) {
					createNestedPackage(newPackage);
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
				if ((newClass == null) && (oldClass != null)) {
					removeClass(oldClass);
				}
				if ((newPackage == null) && (oldPackage != null)) {
					removeNestedPackage(oldPackage);
				}
				break;
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

	@Override
	public void rename(String nm) {
		RenamingRunnable runnable = new RenamingRunnable(nm);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	public IClassService<?, ?> createClass(Class newClass) {
		IClassService<?, ?> cl = null;
		ClassCreation runnable = new ClassCreation(newClass);
		/*
			PlatformUI.getWorkbench().getProgressService().run(
					false, 
					true,
					runnable
			);
		 */
		CoffeaUML2Plugin.getInstance().execute(runnable);
		cl  = runnable.getResult();
		return cl;
	}

	public IPackageService createNestedPackage(Package newPackage) {
		IPackageService pckH = null;
		NestedPackageCreation runnable = 
			new NestedPackageCreation(newPackage, this);
		/*
			PlatformUI.getWorkbench().getProgressService().run(
					false, 
					true,
					runnable
			);
		 */
		CoffeaUML2Plugin.getInstance().execute(runnable);
		pckH  = runnable.getResult();
		return pckH;
	}
	
	public IPackageService createNestedPackage(String simpleName) {
		IPackageService pckH = null;
		Package newPackage = 
			getUMLElement().createNestedPackage(simpleName);
		NestedPackageCreation runnable = 
			new NestedPackageCreation(newPackage, this);
		CoffeaUML2Plugin.getInstance().execute(runnable);
		pckH  = runnable.getResult();
		return pckH;
	}

	public void removeClass(Class oldClass) {
		ClassRemoval runnable = new ClassRemoval(oldClass);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	public void removeNestedPackage(Package oldPackage) {
		NestedPackageRemoval runnable = new NestedPackageRemoval(oldPackage);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	/** Class creation runnable */
	public class ClassCreation 
	extends AbstractUMLToCodeModificationRunnable
	<Class, IClassService<?, ?>> {

		/** Uninitialized class creation */
		public ClassCreation() {
		}

		/**
		 * Class creation
		 * @param c
		 * Value of {@link #objective}
		 */
		public ClassCreation(Class c) {
			this();
			objective = c;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			if (objective != null) {
				String simpleName = ClassService
				.simpleNameExtraction(objective);
				IModelService model = getModelService();
				IJavaElementServiceBuilding builder = null;
				if (model != null) {
					builder = model.getServiceBuilder();
				}
				if ((simpleName != null) && (simpleName.length() > 0)
						&& (builder != null)) {
					try {
						String fileName = 
							simpleName 
							+ 
							Resources.getCodeConstant(
									"constants.javaSourceFileExtension"
							);
						String content = new String();
						String fullName = getFullName();
						if(
								(fullName!=null)
								&&(
										!fullName.equals(
												IModelService
												.defaultPackageFileName
										)
								)
						) {
							content += 
								Resources.getCodeConstant(
										"constants.package"
								)
								+ ' '
								+ getFullName() 
								+ 
								Resources.getCodeConstant(
										"constants.endStatement"
								)
								+ 
								Resources.getCodeConstant(
										"constants.newLine"
								);
						}
						content += '\n' + "public class " + simpleName + '{';
						content += '\n' + "}";
						ICompilationUnit unit = 
							javaElement.createCompilationUnit(
									fileName, 
									content, 
									true,
									monitor
							);
						ITypeService<?, ?> tp = 
							builder.processTypeService(unit, monitor);
						if (tp instanceof IClassService<?, ?>) {
							result = (IClassService<?, ?>) tp;
							addTypeService(result);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/** Class creation runnable */
	public class NestedPackageCreation 
	extends AbstractUMLToCodeModificationRunnable
	<Package, IPackageService> {

		/** Handler for the nesting package */
		private IPackageService nestingPackageHandler;

		/** Uninitialized class creation */
		public NestedPackageCreation() {
		}

		/**
		 * Nested package creation
		 * @param c
		 * Value of {@link #objective}
		 * @param nestP
		 * Value of {@link #nestingPackageHandler}
		 */
		public NestedPackageCreation(Package p, IPackageService nestP) {
			this();
			objective = p;
			nestingPackageHandler = nestP;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			if (objective != null) {
				String simpleName = simpleNameExtraction(objective);
				if(javaElement!=null) {	
					IJavaElement parent = 
						javaElement.getParent();
					if(
							parent 
							instanceof IPackageFragmentRoot
					) {
						if(defaultUMLPackageName.equals(simpleName)) {
							try {
								objective.setName(
										adjustedDefaultUMLPackageName
								);
							} catch(IllegalStateException e) {

							}
							simpleName = adjustedDefaultUMLPackageName;
						}
						IPackageFragmentRoot fragRoot = 
							(IPackageFragmentRoot)parent;
						String qualifiedName = new String();
						IContainerService contH = getContainerService();
						if(contH != null) {
							String parentFullName = contH.getFullName();
							if(
									(parentFullName!=null)
									&&(
											!parentFullName.equals(
													ModelService
													.defaultPackageFileName
											)
									)
							){
								qualifiedName = 
									parentFullName + '.' + simpleName;
							}
							else {
								qualifiedName = simpleName;
							}
						}
						else {
							qualifiedName = simpleName;
						}
						try {
							IPackageFragment fragment = 
								fragRoot.createPackageFragment(
										qualifiedName, 
										true, 
										monitor
								);
							if(fragment!=null) {
								result = 
									new PackageService(
											fragment, 
											nestingPackageHandler, 
											objective
									);
							}
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/** Class removal runnable */
	public class ClassRemoval 
	extends AbstractUMLToCodeModificationRunnable
	<Class, IClassService<?, ?>> {

		/** Uninitialized class removal */
		public ClassRemoval() {
		}

		/**
		 * Class creation
		 * @param c
		 * Value of {@link #objective}
		 */
		public ClassRemoval(org.eclipse.uml2.uml.Class c) {
			this();
			objective = c;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (objective != null) {
				String simpleName = ClassService
				.simpleNameExtraction(objective);
				String qualifiedName = getFullName() + '.' + simpleName;
				ITypeService<?, ?> tp = resolveTypeService(qualifiedName);
				if (tp != null) {
					IType jEl = tp.getJavaElement();
					if (jEl != null) {
						try {
							jEl.getCompilationUnit().delete(
									false, 
									monitor
							);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/** Nested package removal runnable */
	public class NestedPackageRemoval 
	extends AbstractUMLToCodeModificationRunnable
	<Package, IPackageService> {

		/** Uninitialized nested package removal */
		public NestedPackageRemoval() {
		}

		/**
		 * Nested Package removal
		 * @param c
		 * Value of {@link #objective}
		 */
		public NestedPackageRemoval(Package p) {
			this();
			objective = p;
		}

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if (objective != null) {
				String simpleName = simpleNameExtraction(objective);
				String fullName = getFullName();
				String qualifiedName = null;
				if(
						(fullName!=null)
						&&(!fullName.equals(ModelService.defaultPackageFileName))
				) {
					qualifiedName = getFullName() + '.' + simpleName;
				}
				else {
					qualifiedName = simpleName;
				}
				IPackageService packH = resolvePackageService(qualifiedName);
				if (packH != null) {
					IPackageFragment jEl = packH.getJavaElement();
					if (jEl != null) {
						try {
							jEl.delete(
									true, 
									monitor
							);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
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
				JavaRenameProcessor p;
				try {
					p = new RenamePackageProcessor(javaElement);
					String qualifiedName = getQualifiedNewName();
					RenameSupport support = 
							RenameSupport.create(javaElement, qualifiedName, -1);
					p.setNewElementName(qualifiedName);
					Refactoring r = new RenameRefactoring(p);
					PerformRefactoringOperation op = 
						new PerformRefactoringOperation(
								r, 
								CheckConditionsOperation.FINAL_CONDITIONS
						);
					op.run(monitor);
					Object newElement = p.getNewElement();
					if(newElement instanceof IPackageFragment) {
						javaElement = (IPackageFragment)newElement;
					}
					/*
						ITypesContainerHandling contH = getContainerHandler();
						if(contH!=null) {
							String newFullName = contH.getFullName();
							if(
									(newFullName!=null)
									&&(
											!newFullName.equals(
													ModelHandler
													.defaultPackageName
											)
									)
							) {
								newFullName += '.' + newName;
							}
							else {
								newFullName = newName;
							}
							IModelHandling mdH = getModelHandler();
							IJavaProject project = mdH.getJavaProject();
							if(project!=null) {
								IProject prj = project.getProject();
								if(prj!=null) {
									// Unable to retrieve the new element now
									if(contH instanceof IPackageHandling) {
										IPackageHandling parentPackH = 
											(IPackageHandling)contH;
										parentPackH.getJavaElement().
									}
									IResource res = 
										prj.findMember(newFullName);
									if(res!=null) {
										IPath path = 
											res.getProjectRelativePath();
										if(path!=null) {
											IPackageFragment fragment = 
												project.findPackageFragment(
														path
												);
											javaElement = fragment;
										}
									}
								}
							}
						}
					 */
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		private String getQualifiedNewName() {
			String qualifiedName = null;
			IContainerService contH = getContainerService();
			if(contH!=null) {
				String parentFullName = contH.getFullName();
				if(
						(parentFullName!=null)
						&&(
								!parentFullName.equals(
										ModelService
										.defaultPackageFileName
								)
						)
				) {
					qualifiedName = parentFullName + '.' + newName;
				}
				else {
					qualifiedName = newName;
				}
			}
			else {
				qualifiedName = newName;
			}
			return qualifiedName;
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