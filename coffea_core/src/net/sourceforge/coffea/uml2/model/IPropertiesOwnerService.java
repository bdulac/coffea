package net.sourceforge.coffea.uml2.model;

import java.util.List;

import org.eclipse.uml2.uml.Property;

/** Service for an element owning properties */
public interface IPropertiesOwnerService extends IOwnerService {

	/**
	 * @return List of services handling the properties belonging to the
	 * owner
	 */
	public List<IAttributeService> getPropertiesServices();
	
	/**
	 * Returns the service for a property given the property simple name
	 * @param n
	 * Property simple name
	 * @return Service for the attribute corresponding to the name
	 * @see IElementService#getSimpleName()
	 */
	public IAttributeService getPropertyService(String n);
	
	/**
	 * Creates in the code a property owned by the handled owner
	 * @param p
	 * Property to create
	 * @return Service for the created property
	 */
	public IAttributeService createProperty(Property p);
	
	/**
	 * Deletes from the code a property owned by the handled owner
	 * @param p
	 * Property to delete
	 */
	public void deleteProperty(Property p);
}
