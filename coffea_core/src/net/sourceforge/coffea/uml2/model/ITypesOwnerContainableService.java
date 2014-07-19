package net.sourceforge.coffea.uml2.model;

/** Service for an element containable in a types owner */
public interface ITypesOwnerContainableService extends IOwnerService {

	/**
	 * Returns the service for the types owner containing the handled element
	 * @return Service for the types owner containing the handled element
	 */
	public ITypesContainerService getContainerService();
	
}
