package net.sourceforge.coffea.uml2.model;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Type;

/**
 * Service for a type
 * @param <S>
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public interface ITypeService
<S extends TypeDeclaration, J extends IType> 
extends IPackageableElementService<S, J>, ITypesOwnerContainableService {

	/**
	 * Sets the service for the element containing the type service
	 * @param typesCtrSrv
	 * Service for the element 
	 */
	public void setContainerService(ITypesContainerService typesCtrSrv);
	
	/**
	 * Reconstruction of a name known by the classifier from its part
	 * @param nm
	 * Name parts
	 * @return Reconstructed name
	 */
	public String nameReconstruction(String[][] nm);
	
	/** @return Service for the type super type */
	public ITypeService<?, ?> getSuperTypeService();
	
	/** @return List of services for the type super interfaces */
	public List<IInterfaceService<?, ?>> getSuperInterfaceServices();
	
	public Type getUMLElement();
}