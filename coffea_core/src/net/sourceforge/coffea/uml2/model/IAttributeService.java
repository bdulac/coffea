package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.uml2.uml.Property;

/** Service for an attribute */
public interface IAttributeService 
extends 
IMemberService<FieldDeclaration, IField>, 
IStructuralFeatureService, 
IOwnableElementService<FieldDeclaration, IField> {

	/**
	 * Returns the attribute handled by the service
	 * @return Attribute handled by the service
	 */
	public Property getUMLElement();
	
	/**
	 * Resolves the service for the type containing the handled attribute
	 * @return Service for the type containing the handled attribute
	 */
	public ITypeService<?, ?> resolveTypeService();

	/**
	 * Renames the attribute in the code
	 * @param nm
	 * New simple name
	 */
	public void rename(String nm);
}
