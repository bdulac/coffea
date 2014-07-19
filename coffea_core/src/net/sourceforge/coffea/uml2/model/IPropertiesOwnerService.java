package net.sourceforge.coffea.uml2.model;

import java.util.List;

import org.eclipse.uml2.uml.Property;

/** Service for an element owning properties */
public interface IPropertiesOwnerService extends IOwnerService {

	/**
	 * Returns the list of services handling the properties belonging to the
	 * handled owner
	 * @return List of services handling the properties belonging to the
	 * handled owner
	 */
	public List<IAttributeService> getPropertiesServices();
	
	/**
	 * Returns service for a property owned by the handled owner given the 
	 * property name
	 * @param n
	 * Property simple name
	 * @return Service corresponding to the name
	 * @see IElementService#getSimpleName()
	 */
	public IAttributeService getPropertyService(String n);
	
	/**
	 * Adds an service to the list of services handling the properties 
	 * belonging to the handled owner
	 * @param prH
	 * New service to add to the list
	 */
	public void addPropertyService(IAttributeService prH);
	
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
