package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.model.IAttributeService;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IContainableElementService;
import net.sourceforge.coffea.uml2.model.IContainerService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.IPropertiesOwnerService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.impl.MemberService;
import net.sourceforge.coffea.uml2.model.impl.PropertyService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.eclipse.jdt.internal.corext.refactoring.sef.SelfEncapsulateFieldRefactoring;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;

/** Service for an attribute */
public class PropertyService 
extends MemberService<Property, FieldDeclaration, IField> 
implements IAttributeService {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -7692912393329783975L;

	/**
	 * Resolves an UML property fully qualified name
	 * @param prop
	 * UML property for which the fully qualified name must be resolved
	 * @return UML property fully qualified name
	 */
	public static String buildFullyQualifiedName(Property prop) {
		String name = new String();
		if(prop != null) {
			Element owner = prop.getOwner();
			if(owner instanceof Type) {
				Type cl = (Type)owner;
				String containerName = 
					ClassifierService.buildFullyQualifiedName(cl);
				name = containerName + '#' + codeSimpleNameExtraction(prop);
			}

		}
		return name;
	}

	/**
	 * Builds a Java type fully qualified name
	 * @param element
	 * Type to build the full name from
	 * @return Type fully qualified name
	 */
	public static String buildFullyQualifiedName(IField f) {
		String fullName = null;
		if(f != null) {
			IJavaElement parent = f.getParent();
			if(parent instanceof ICompilationUnit) {
				parent = parent.getParent();
			}
			if(parent instanceof IType) {
				fullName = 
					ClassifierService.buildFullyQualifiedName((IType)parent);
				fullName += '#' + buildSimpleName(f);
			}
		}
		return fullName;
	}

	/**
	 * Builds a Java field simple name
	 * @param f
	 * Java field to build the simple name from
	 * @return Java field simple name
	 */
	public static String buildSimpleName(IField f) {
		String simpleName = null;
		if(f != null) {
			simpleName = f.getElementName();
		}
		return simpleName;
	}

	/**
	 * Builds a field declaration simple name
	 * @param f
	 * Field declaration to build the simple name from
	 * @return Field declaration simple name
	 */
	public static String buildSimpleName(FieldDeclaration f) {
		String simpleName = null;
		if(f != null) {
			// Adjusting the declared name which is, in the AST syntax 
			// point of view, followed by the '=' character
			String declaredName = f.fragments().get(0).toString();
			if(declaredName.lastIndexOf('=')!=-1)
				declaredName = 
					declaredName.substring(0, declaredName.indexOf('='));
			simpleName = declaredName;
		}
		return simpleName;
	}

	/**
	 * Java class simple name extraction from the corresponding UML element
	 * @param p
	 * UML element from which the name will be extracted
	 * @return Extracted simple name
	 */
	public static String codeSimpleNameExtraction(Property p) {
		String simpleName = null;
		if(p != null) {
			simpleName = p.getName();

		}
		return simpleName;
	}

	/**
	 * Construction of a service for an attribute from an AST node
	 * @param clSrv
	 * 	Value of {@link #container}
	 * @param fDeclaration
	 * 	Value of {@link #syntaxTreeNode}
	 */
	protected PropertyService(
			FieldDeclaration fDeclaration, 
			IClassService<?, ?> clSrv
	) {
		super(fDeclaration, clSrv);
		completePropertyConstruction(null, clSrv);
	}

	/**
	 * Construction of a service for an attribute from a Java element
	 * @param clSrv
	 * 	Value of {@link #container}
	 * @param jEl
	 * 	Value of {@link #javaElement}
	 */
	protected PropertyService(IField jEl, IClassService<?, ?> clSrv) {
		super(jEl, clSrv);
		completePropertyConstruction(null, clSrv);
	}
	
	protected void completePropertyConstruction(
			ASTRewrite r, 
			IPropertiesOwnerService p
	) {
	}

	public String getSimpleName() {
		String simpleName = null;
		if(syntaxTreeNode != null) {
			simpleName = buildSimpleName(syntaxTreeNode);
		}
		else if(javaElement != null) {
			simpleName = buildSimpleName(javaElement);
		}
		return simpleName;
	}
	
	@Override
	public IClassService<?, ?> getContainerService() {
		return (IClassService<?, ?>)super.getContainerService();
	}

	public ITypeService<?, ?> resolveTypeService() {
		String typeName = null;
		ITypeService<?, ?> tHandler = null;
		boolean imported = false;
		if(syntaxTreeNode != null) {
			if(syntaxTreeNode.getType() instanceof SimpleType) {
				SimpleType supplierType = 
						(SimpleType)syntaxTreeNode.getType();
				ITypeBinding binding = supplierType.resolveBinding();
				if(binding != null) {
					typeName = binding.getQualifiedName();
				}
			}
		}
		else if(javaElement != null) {
			try {
				IImportDeclaration[] imports = null;
				// Aiming to get the field type fully qualified name, 
				if(container instanceof IClassService<?, ?>) {
					// We try to get the containing class imports
					IClassService<?, ?> cl = (IClassService<?, ?>)container;
					imports = 
						cl.getJavaElement().getCompilationUnit().getImports();
				}
				typeName = javaElement.getTypeSignature();
				if(typeName != null) {
					// If we don't have the type fully qualified name, 
					if(typeName.startsWith("Q")) {
						// We get the simple name
						typeName = Signature.getSignatureSimpleName(typeName);
						ITypeService<?, ?> cont = getContainerService();
						String[][] parts = 
								cont.getJavaElement().resolveType(typeName);
						typeName = cont.nameReconstruction(parts);
						/*
						IImportDeclaration imp = null;
						// And try to resolve the full name from the imports
						if(imports != null) {
							for(int i = 0 ; i < imports.length ; i++) {
								imp = imports[i];
								if(imp.getElementName().endsWith(typeName)) {
									typeName = imp.getElementName();
									imported = true;
									break;
								}
							}
						}
						*/
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		if(typeName != null) {
			// If the type has been imported, 
			if(imported) {
				// Then we look for it in the model
				tHandler = getModelService().resolveTypeService(typeName);
			}
			else {
				// Else we got to get the local package
				IPackageService packSrv = null;
				IContainerService contSrv = getContainerService();
				while(
						(contSrv != null)
						&&(!(contSrv instanceof IPackageService))
						&&(contSrv instanceof IContainableElementService<?, ?>)
				) {

					contSrv = 
						((IContainableElementService<?, ?>)contSrv)
						.getContainerService();
				}
				// From the package we can have the type
				if(contSrv instanceof IPackageService) {
					packSrv = (IPackageService)contSrv;
					String packName = packSrv.getFullName();
					String fullName = packName + '.' + typeName;
					tHandler = packSrv.resolveTypeService(fullName);
				}
			}
		}
		return tHandler;
	}

	private void loadExistingUmlElement() {
		String name = getSimpleName();
		Classifier classif = getContainerService().getUMLElement();
		ITypeService<?, ?> typeSrv = resolveTypeService();
		Type umlType = null;
		if(typeSrv != null){
			umlType = typeSrv.getUMLElement();
		}
		if(classif instanceof Class) {
			Class cla = (Class)classif;
			umlModelElement = cla.getOwnedAttribute(name, umlType);
		}
		else if(classif instanceof Enumeration) {
			Enumeration en = (Enumeration)classif;
			en.getAttribute(name, umlType);
		}
	}

	private void createUmlElement() {
		//FIXME missing parameters
		if(getContainerService() != null) {
			ITypeService<?, ?> typeSrv = resolveTypeService();
			Type umltype = null;
			if(typeSrv!=null){
				umltype = typeSrv.getUMLElement();
			}
			Classifier classif = getContainerService().getUMLElement();
			if (classif instanceof Class) {
				Class cla = (Class) classif;
				umlModelElement = 
						cla.createOwnedAttribute(getSimpleName(),umltype);
			}
			else if(classif instanceof Enumeration) {
				Enumeration en = (Enumeration)classif;
				umlModelElement = 
						en.createOwnedAttribute(getSimpleName(), umltype);
			}
			umlModelElement.setVisibility(getVisibility());
		}
	}
	
	public void setUpUMLModelElement() {
		if(umlModelElement == null)loadExistingUmlElement();
		if(umlModelElement == null) {
			createUmlElement();
		}
	}

	@Override
	public void rename(String nm) {
		FieldRenamingRunnable runnable = new FieldRenamingRunnable(nm);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	@Override
	public void changeVisibility(String visibilityLiteral) {
		if(javaElement instanceof IField) {
			IField field = (IField)javaElement;
			try {
				SelfEncapsulateFieldRefactoring encapsulation = 
					new SelfEncapsulateFieldRefactoring(field);
				int visiblityInt = VisibilityKind.PRIVATE;
				if(
						visibilityLiteral.equals(
								VisibilityKind
								.PRIVATE_LITERAL
								.getLiteral()
						)
				) {
					// visiblityInt = Flags.AccPrivate;
					visiblityInt = Flags.AccPublic;
				}
				else if(
						visibilityLiteral.equals(
								VisibilityKind
								.PROTECTED_LITERAL
								.getLiteral()
						)
				) {
					visiblityInt = Flags.AccProtected;
				}
				else if(
						visibilityLiteral.equals(
								VisibilityKind
								.PUBLIC_LITERAL
								.getLiteral()
						)
				) {
					visiblityInt = Flags.AccPublic;
				}
				encapsulation.setVisibility(visiblityInt);
				encapsulation.setGetterName("get" + field.getElementName());
				encapsulation.setSetterName("set" + field.getElementName());
				// encapsulation.setGenerateJavadoc(true);
				// encapsulation.setConsiderVisibility(true);
				Refactoring r = encapsulation;
				PerformRefactoringOperation op = 
					new PerformRefactoringOperation(
							r, 
							CheckConditionsOperation.INITIAL_CONDITONS
					);
				op.run(new NullProgressMonitor());
				/*
				 * 	PerformRefactoringOperation op = 
				 * 		new PerformRefactoringOperation(
				 * 			r, 
				 * 			CheckConditionsOperation
				 * 			.INITIAL_CONDITONS
				 * 	);
				 * 	op.run(monitor);
				 */

			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/** Field renaming runnable */
	public class FieldRenamingRunnable implements IUML2RunnableWithProgress {

		/** New simple name */
		protected String newName;

		/**
		 * Renaming runnable construction
		 * @param nm
		 * Value of {@link #newName}
		 */
		public FieldRenamingRunnable(String nm) {
			newName = nm;
		}

		public void run(IProgressMonitor monitor) {
			if((javaElement!=null)&&(newName!=null)) {
				RenameFieldProcessor p;
				try {
					p = new RenameFieldProcessor(javaElement);
					p.setNewElementName(newName);
					Refactoring r = new RenameRefactoring(p);
					PerformRefactoringOperation op = 
						new PerformRefactoringOperation(
								r, 
								CheckConditionsOperation.FINAL_CONDITIONS
						);
					op.run(monitor);
					// We get the parent java element
					IJavaElement parent = javaElement.getParent();
					if(parent instanceof IType) {
						IType t = (IType)parent;
						// From its children, we get the new property
						IField[] fields = t.getFields();
						if(fields!=null) {
							IField f = null;
							for(int i=0 ; i<fields.length ; i++) {
								f = fields[i];
								if(f!=null) {
									String n = f.getElementName();
									if((n!=null)&&(n.equals(newName))) {
										javaElement = f;
										break;
									}
								}
							}
						}
					}
					// javaElement = p.getField();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** Type changing runnable */
	public class TypeChangingRunnable implements IUML2RunnableWithProgress {

		/** New type */
		protected Class newName;

		/**
		 * Renaming runnable construction
		 * @param nm
		 * Value of {@link #newName}
		 */
		public TypeChangingRunnable(Class nm) {
			newName = nm;
		}

		public void run(IProgressMonitor monitor) 
		throws InvocationTargetException, InterruptedException {
			if((javaElement!=null)&&(newName!=null)) {
				// RenameFieldProcessor p = null;
				try {
					Refactoring r = 
						null
						/*new ChangeTypeRefactoring(
								null, 
								1, 
								1, 
								1
						)*/;
					
					PerformRefactoringOperation op = 
						new PerformRefactoringOperation(
								r, 
								CheckConditionsOperation.FINAL_CONDITIONS
						);
					op.run(monitor);
					// We get the parent java element
					IJavaElement parent = javaElement.getParent();
					if(parent instanceof IType) {
						IType t = (IType)parent;
						// From its children, we get the new property
						IField[] fields = t.getFields();
						if(fields!=null) {
							IField f = null;
							for(int i=0 ; i<fields.length ; i++) {
								f = fields[i];
								if(f!=null) {
									String n = f.getElementName();
									if((n!=null)&&(n.equals(newName))) {
										javaElement = f;
										break;
									}
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