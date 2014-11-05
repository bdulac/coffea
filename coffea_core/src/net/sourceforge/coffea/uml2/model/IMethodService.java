package net.sourceforge.coffea.uml2.model;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.uml2.uml.Operation;

/** Service for a method */
public interface IMethodService 
extends 
IMemberService<MethodDeclaration, IMethod>, 
IBehavioralFeatureService, 
IOwnableElementService<MethodDeclaration, IMethod> {
	
	/** @return Method handled by the service */
	public Operation getUMLElement();
	
	/**
	 * Resolves a service for the handled method return type
	 * @return Service for the handled method return type
	 */
	public ITypeService<?, ?> resolveReturnTypeService();
	
	/**
	 * Returns the service for the method return type handler if it has 
	 * {@link #resolveReturnTypeService() previously been resolved}, otherwise 
	 * proceeds a resolution first
	 * @return Service for the handled method return
	 */
	public ITypeService<?, ?> getReturnTypeHandler();
	
	/**
	 * Renames the method in the code
	 * @param nm
	 * New simple name
	 */
	public void rename(String nm);
	
	/**
	 * Resolves the method parameters names
	 * @return List of the method parameters names
	 */
	public List<String> resolveParametersNames();
	
	/** 
	 * Returns the method parameters names if they have not
	 * {@link #resolveParametersNames() been previously resolved}, 
	 * otherwise proceeds a resolution first
	 * @return List of the method parameter names
	 */
	public List<String> getParametersNames();
	
	/**
	 * Resolves services for the handled method parameters types
	 * @return Services for the handled method parameters types
	 */
	public List<ITypeService<?, ?>> resolveParametersTypesServices();
	
	/** 
	 * Returns the method parameters types handlers if they have not 
	 * {@link #resolveParametersTypesServices() been previously resolved}, 
	 * otherwise proceeds a resolution first
	 * @return List of the method parameter types handlers
	 */
	public List<ITypeService<?, ?>> getParametersTypesServices();
	
}