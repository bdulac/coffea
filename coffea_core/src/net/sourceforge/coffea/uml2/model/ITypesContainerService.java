package net.sourceforge.coffea.uml2.model;

import java.util.List;

/** Service for an element containing types */
public interface ITypesContainerService 
extends ITypesOwnerContainableService, IGroupService {

	/**
	 * Returns the list of services for the types contained in the handled 
	 * element
	 * @return List of services for the types belonging to the handled
	 * types container
	 */
	public abstract List<ITypeService<?, ?>> getTypesServices();

	/**
	 * Adds a service to the list of services for the types contained in the 
	 * handled element
	 * @param typeSrv
	 * New service to add to the list
	 */
	public abstract void addTypeService(ITypeService<?, ?> typeSrv);
	
	/**
	 * Resolves a {@link ITypeService service} for a contained type given its 
	 * full name
	 * @param n
	 * {@link ITypeService} full name
	 * @return Service for the given full name
	 */
	public ITypeService<?, ?> resolveTypeService(String n);
}