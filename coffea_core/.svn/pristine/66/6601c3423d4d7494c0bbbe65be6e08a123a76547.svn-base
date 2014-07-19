package net.sourceforge.coffea.uml2.model;

import java.util.List;

import org.eclipse.uml2.uml.Element;

/** Service for a container : an element containing other elements */
public interface IContainerService extends IElementService {

	/**
	 * Returns a service for an element contained in this handled container 
	 * given the contained element name
	 * @param n
	 * Name of the contained element for which a service must be returned
	 * @return Service corresponding to the given element name
	 */
	public IElementService getElementService(String n);
	
	/**
	 * Returns a service for an element contained in this handled container 
	 * given the contained element
	 * @param el
	 * Contained element for which a service must be returned
	 * @return Service corresponding to the given element
	 */
	public IElementService getElementHandler(Element el);
	
	/**
	 * Returns a list of all services for the elements contained in the handled
	 * container
	 * @return List of all services
	 */
	public List<IElementService> getElementsHandlers();
}
