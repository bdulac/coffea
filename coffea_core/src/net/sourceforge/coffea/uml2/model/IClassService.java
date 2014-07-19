package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Class;

/** 
 * Class service
 * <p>
 *     Implements member service capabilities to admit the cases of nested 
 *     classes.
 * </p>
 * @param <S>
 * Type of the class handled by the service as AST node
 * @param <J>
 * Type of the class handled by the service as Java element
 */
public interface IClassService<S extends TypeDeclaration, J extends IType> 
extends 
ITypesOwnerContainableService, 
IPropertiesOwnerService,
ITypesContainerService, 
IInterfaceService<S, J>, 
IMemberService<S, J> {
	/*
	 * This quite dirty to consider classes as types containers as these are 
	 * only nested classes containers but this is a choice to avoid 
	 * multiplying interfaces (around ITypesContainerService, 
	 * ITypesOwnerContainableService etc.)
	 */	
	
	/**
	 * Returns the class handled by the service
	 * @return Class handled by the service
	 */
	public Class getUMLElement();
	
	/**
	 * In the code creates a class nested in the class handled by the service
	 * @param newClass
	 * Nested class to create
	 * @return Service for the nested class created
	 */
	public IClassService<?, ?> createNestedClass(Class newClass);

	/**
	 * In the code deletes a class nested in the class handled by the service
	 * @param oldClass
	 * Nested class to delete
	 */
	public void deleteNestedClass(Class oldClass);
}
