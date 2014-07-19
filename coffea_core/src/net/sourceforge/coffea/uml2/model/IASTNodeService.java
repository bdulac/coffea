package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

/** 
 * Service for an AST node
 * @param <S>
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public interface IASTNodeService<S extends ASTNode, J extends IJavaElement> {
	
	/**
	 * Returns the AST node handled by the service as an AST node
	 * @return AST node handled by the service
	 */
	public S getSyntaxNode();
	
	/**
	 * Returns the Java element handled by the service as a Java Element
	 * @return Java element handled by the service
	 */
	public J getJavaElement();
	
}
