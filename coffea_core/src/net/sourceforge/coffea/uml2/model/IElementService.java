package net.sourceforge.coffea.uml2.model;

import java.io.Serializable;

import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.creation.IModelServiceBuilding;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.uml2.uml.Element;

/** Service for an element */
public interface IElementService extends Serializable, ResourceSetListener {

	/** Attribute name for an element name */
	public static final String NAME_ATTRIBUTE = 
		Resources.getParameter("constants.emfNameAttribute");
	
	/**
	 * Returns the builder which has built the service
	 * @return Service builder
	 */
	public IModelServiceBuilding getServiceBuilder();
	
	/** 
	 * Returns the service for the model to which the handled element belongs
	 * @return Service for the model to which the handled element belongs
	 */
	public IModelService getModelService();
	
	/** 
	 * Sets up the UML element: loads it if is already registered in the 
	 * model, creates it otherwise
	 * @see #getModelService() 
	 */
	public void setUpUMLModelElement();

	/**
	 * Returns the element handled by the service
	 * @return Element handled by the service
	 */
	public Element getUMLElement();

	/**
	 * Returns the element simple name
	 * @return Element simple name
	 */
	public String getSimpleName();
	
	/**
	 * Returns the element full name
	 * @return Element full name
	 */
	public String getFullName();
	
	/**
	 * Finds the editor UML element corresponding to the handled element
	 * @return Editor UML element having a full name equals to the handled 
	 * element
	 * @see #getUMLElement()
	 */
	public Element findEditorUMLElement();
	
	/**
	 * Accepts a dispatched model change notification. <b>This method is not 
	 * intended to be called directly</b> : it is commonly called by the 
	 * resource change {@link #resourceSetChanged method}
	 * @param nt
	 * Model change notification to accept
	 */
	public void acceptModelChangeNotification(Notification nt);
	
	/**
	 * Renames the element in the code
	 * @param nm
	 * New fully qualified name
	 */
	public void rename(String nm);
}