package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.uml2.uml.Classifier;

/** 
 * Service for a classifier
 * @param <S>
 * Type of the classifier handled by the service as AST node
 * @param <J>
 * Type of the classifier handled by the service as Java element
 */
public interface IClassifierService
<S extends TypeDeclaration, J extends IType> 
extends ITypeService<S, J>, IOperationsOwnerService, IPropertiesOwnerService, 
ITypesOwnerContainableService, ITypesContainerService {
	
	/**
	 * Returns the classifier handled by the service
	 * @return Classifier handled by the service
	 */
	public Classifier getUMLElement();
	
	/**
	 * Returns the rewriter for the compilation unit the classifier belongs 
	 * to
	 * @return Rewriter for the compilation unit the classifier belongs to
	 */
	public ASTRewrite getRewriter();

}