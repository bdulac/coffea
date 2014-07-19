package net.sourceforge.coffea.uml2.model;

import net.sourceforge.coffea.uml2.Resources;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.uml2.uml.VisibilityKind;

/** 
 * Service for a member of a class
 * @param <S>
 * Type of the member handled by the service as AST node
 * @param <J>
 * Type of the member handled by the service as Java element
 */
public interface IMemberService
<S extends BodyDeclaration, J extends IJavaElement> 
extends IOwnableElementService<S, J> {

	/** Attribute name for an element visibility */
	public static final String VISIBILITY_ATTRIBUTE = 
		Resources.getParameter("constants.emfVisibilityAttribute");
	
	public S getSyntaxNode();

	/**
	 * Returns the member visibility
	 * @return Visibility
	 */
	public VisibilityKind getVisibility();

	/**
	 * Returns <code>true</code> if the member is static, else
	 * <code>false</code>
	 * @return <code>true</code> if the member is static, else
	 *         <code>false</code>
	 */
	public boolean isStatic();

	/**
	 * Returns <code>true</code> if the element is abstract, else
	 * <code>false</code>
	 * @return <code>true</code> if the element is abstract, else
	 *         <code>false</code>
	 */
	public boolean isAbstract();
	
	/**
	 * Changes the member visibility in the code
	 * @param visibilityLiteral
	 * New visibility kind {@link VisibilityKind#getLiteral() literal value}
	 */
	public void changeVisibility(String visibilityLiteral);

}