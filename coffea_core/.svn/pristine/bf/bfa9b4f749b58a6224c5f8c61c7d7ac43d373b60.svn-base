package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.uml2.uml.PackageableElement;

/** 
 * Service for an element which can be owned by a package
 * @param <S>
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public interface IPackageableElementService
<S extends ASTNode, J extends IJavaElement>
extends IContainableElementService<S, J> {
	
	public PackageableElement getUMLElement();

}