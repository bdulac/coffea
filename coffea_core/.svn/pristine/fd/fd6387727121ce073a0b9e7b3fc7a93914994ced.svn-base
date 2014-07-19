package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

/** 
 * Service for an element which can be contained in another element
 * @param <S>
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public interface IContainableElementService
<S extends ASTNode, J extends IJavaElement> 
extends IElementService, IASTNodeService<S, J> {

	/**
	 * Returns a service for the element containing the handled element
	 * @return Service for the element containing the handled element
	 */
	public IContainerService getContainerService();
	
}
