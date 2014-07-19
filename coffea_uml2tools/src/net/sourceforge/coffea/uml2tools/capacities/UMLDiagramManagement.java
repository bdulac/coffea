package net.sourceforge.coffea.uml2tools.capacities;

import org.eclipse.uml2.uml.Element;

/** Management of an UML diagram edition */
public interface UMLDiagramManagement {
	
	/**
	 * Finds the editor UML element corresponding to the handled element
	 * @return Editor UML element having a full name equals to the handled 
	 * element
	 * @see #getUMLElement()
	 */
	public Element findEditorUMLElement();

}
