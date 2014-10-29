package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.creation.IModelServiceBuilding;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

/** 
 * Service for an element
 * @param <E> 
 * Type of the element handled by the service as UML element
 */
public abstract class ElementService<E extends Element> 
implements IElementService {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = 3970820673328182566L;

	/**
	 * Builds an UML element fully qualified name
	 * @param el
	 * UML element to build the name from
	 * @return UML element fully qualified name
	 */
	public static String buildFullyQualifiedName(Element el) {
		String name = null;
		if(el instanceof Package) {
			name = PackageService.buildFullyQualifiedName((Package)el);
		}
		else if(el instanceof Type) {
			name = ClassifierService.buildFullyQualifiedName((Type)el);
		}
		else if(el instanceof Property) {
			name = PropertyService.buildFullyQualifiedName((Property)el);
		}
		return name;
	}

	/** UML element corresponding to the element handled by the service */
	protected E umlModelElement;

	/** Construction of a service for an unspecified element */
	public ElementService() {
	}

	/**
	 * Construction of a service for an element from an UML element
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	public ElementService(E ume) {
		this();
		umlModelElement = ume;
	}

	public E getUMLElement() {
		if(umlModelElement==null) {
			setUpUMLModelElement();
		}
		return umlModelElement;
	}
	
	/** Loading an exiting UML element */
	protected abstract void loadExistingUmlElement();
	
	/** Creation of a new UML element */
	protected abstract void createUmlElement();
	
	/**
	 * This default implementation can be specialized if the element has 
	 * children or parameters
	 * @see IElementService#setUpUMLModelElement()
	 */
	public void setUpUMLModelElement() {
		if(umlModelElement == null)loadExistingUmlElement();
		if(umlModelElement == null) {
			createUmlElement();
		}
	}

	public IModelServiceBuilding getServiceBuilder() {
		IModelServiceBuilding r = null;
		IModelService m = getModelService();
		if(m!=null) {
			r = m.getServiceBuilder();
		}
		return r;
	}

	@Override
	public String toString() {
		return 
				getFullName() 
				+ '#' + super.getClass().getSimpleName() 
				+ "@" + hashCode();
	}

	public boolean isAggregatePrecommitListener() {
		return false;
	}

	public boolean isPostcommitOnly() {
		return true;
	}

	public boolean isPrecommitOnly() {
		return false;
	}

	/*
	public void acceptModelChangeNotification(Notification nt) {
		if(nt!=null) {
			Object feature = nt.getFeature();
			Object newValue = nt.getNewValue();
			Object oldValue = nt.getOldValue();
			if (feature instanceof EAttribute) {
				EAttribute attr = (EAttribute) feature;
				String attrName = attr.getName();
				if((attrName != null)&&(attrName.equals(NAME_ATTRIBUTE))) {
					if (newValue instanceof String) {
						String newName = (String) newValue;
						boolean avoid = false;
						// We avoid renaming if the new value is impossible...
						avoid = 
							(oldValue==null)
							&&(
									newName.equals(
											PackageHandler
											.defaultUMLPackageName
									)
							);
						if(!avoid) {
							rename(newName);
						}
					}
				}
			}
		}
	}
	*/

	public void rename(String n) {

	}
}