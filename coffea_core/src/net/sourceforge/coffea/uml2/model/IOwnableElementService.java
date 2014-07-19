package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

/** 
 * Service for an element which can be owned by another element
 * @param <S>
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public interface IOwnableElementService
<S extends ASTNode, J extends IJavaElement> 
extends IContainableElementService<S, J> {

	/**
	 * Returns the service for the owner of the handled element
	 * @return Service for the owner of the handled element
	 */
	public IOwnerService getContainerService();
	
}
