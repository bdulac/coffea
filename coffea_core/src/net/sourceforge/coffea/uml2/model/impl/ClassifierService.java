package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IClassifierService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.ITypesContainerService;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Type;

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
	
	/**
	 * Classifier service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	protected ClassifierService(ITypesContainerService p, String nm) {
		super(p, nm);
	}
	
	/**
	 * Classifier service construction without any declaration but from an 
	 * existing UML element
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	protected ClassifierService(ITypesContainerService p, String nm, E c) {
		super(p, nm, c);
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
			ITypesContainerService p
	) {
		super(stxNode, p);
	}
	
	/**
	 * Classifier service construction from an AST node and an existing UML 
	 * element
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param p
	 * Value of {@link #container}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	protected ClassifierService(
			S stxNode,
			ITypesContainerService p, 
			E c
	) {
		super(stxNode, p, c);
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
			ITypesContainerService p
	) {
		super(jEl, p);
	}
	
	/**
	 * Classifier service construction from a Java element and and existing 
	 * UML element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param c
	 * Value of {@link #umlModelElement}
	 */
	protected ClassifierService(
			J jEl,
			ITypesContainerService p, 
			E c
	) {
		super(jEl, p, c);
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
}