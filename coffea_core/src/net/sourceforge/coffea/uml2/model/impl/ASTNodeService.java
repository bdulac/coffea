package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IASTNodeService;
import net.sourceforge.coffea.uml2.model.IContainerService;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.uml2.uml.NamedElement;

/** 
 * Service for an AST node
 * @param <E>
 * Type of the element handled by the service as UML element
 * @param <S> 
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public abstract class ASTNodeService
<E extends NamedElement, S extends ASTNode, J extends IJavaElement> 
extends ContainedElementService<E, S, J> 
implements IASTNodeService<S, J> {
	
	/** @see java.io.Serializable */
	private static final long serialVersionUID = 2159210408864922876L;

	/** Node corresponding to the element handled by the service in the AST */
	protected S syntaxTreeNode;

	/** Java element corresponding to the element handled by the service */
	protected J javaElement;
	
	/** 
	 * Default element name (for a case without 
	 * {@link #syntaxTreeNode AST node} or {@link #javaElement Java element})
	 */
	protected String defaultSimpleName;
	
	/**
	 * AST node service construction without any declaration
	 * @param own
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	protected ASTNodeService(IContainerService own, String nm) {
		super(own);
		defaultSimpleName = nm;
	}
	
	/**
	 * AST node service construction without any declaration but with an 
	 * existing UML element
	 * @param own
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	protected ASTNodeService(IContainerService own, String nm, E ume) {
		super(own, ume);
		defaultSimpleName = nm;
	}
	
	/**
	 * AST node service construction from an AST node
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param own
	 * Value of {@link #container}
	 */
	protected ASTNodeService(
			S stxNode, 
			IContainerService own
	) {
		super(own);
		completeConstruction(stxNode);
	}

	/**
	 * AST node service construction from an AST node and an existing UML 
	 * element
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param own
	 * Value of {@link #container}
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	protected ASTNodeService(
			S stxNode,
			IContainerService own, 
			E ume
	) {
		super(own, ume);
		completeConstruction(stxNode);
	}
	
	/**
	 * AST node service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param own
	 * Value of {@link #container}
	 */
	protected ASTNodeService(
			J jEl, 
			IContainerService own
	) {
		super(own);
		completeConstruction(jEl);
	}

	/**
	 * AST node service construction from a Java element and an existing UML
	 * element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param own
	 * Value of {@link #container}
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	protected ASTNodeService(
			J jEl,
			IContainerService own, 
			E ume
	) {
		super(own, ume);
		completeConstruction(jEl);
	}
	
	//Completes the constructors, factorization of the specialized part for 
	//use in every constructor
	private void completeConstruction(S stxNode) {
		syntaxTreeNode = stxNode;
	}
	
	private void completeConstruction(J jEl) {
		javaElement = jEl;
		if(javaElement != null) {
			noteService = new JavadocService<ASTNodeService<E, S, J>>(this);
		}
	}
	
	public S getSyntaxNode() {
		return syntaxTreeNode;
	}
	
	public J getJavaElement() {
		return javaElement;
	}

}