package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.uml2.uml.Association;

/**
 * Service for an association
 * @param <S>
 * Type of the association handled by the service as AST node
 * @param <J>
 * Type of the association handled by the service as Java element
 */
public interface IAssociationService
<S extends BodyDeclaration, J extends IMember> 
extends 
IMemberService<S, J>, 
IStructuralFeatureService, 
IOwnableElementService<S, J> {

	/**
	 * Returns the association handled by the service
	 * @return Association handled by the service
	 */
	public Association getUMLElement();
	
	/**
	 * Resolves the service for the type supplying the association
	 * @return Service for the type supplying the association
	 */
	public ITypeService<?, ?> resolveSupplierService();
	
}
