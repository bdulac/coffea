package net.sourceforge.coffea.uml2.model;

import net.sourceforge.coffea.uml2.Resources;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Package;

/** Service for a package */
public interface IPackageService 
extends 
IPackageableElementService<PackageDeclaration, IPackageFragment>, 
ITypesContainerService, 
IPackagesGroupService {
	
	/** Default UML package name, must be adjusted to be code correct */
	public static final String defaultUMLPackageName = 
		Resources.getParameter("constants.defaultUMLPackageName");

	/** Default UML package name adjusted to be code correct */
	public static final String adjustedDefaultUMLPackageName = 
		Resources.getParameter("constants.adjustedDefaultUMLPackageName");

	public Package getUMLElement();	

	/**
	 * Sets the element group handler
	 * @param gr
	 * Element group handler
	 */
	public void setGroupService(IPackagesGroupService gr);
	
	public IPackagesGroupService getContainerService();
	
	/** 
	 * Retrieves the package container from the package hierarchy ; 
	 * this aim to fetch the package hierarchy which has disappeared between 
	 * the source code directories and the code AST parsing.
	 * @see #getContainerService()
	 * @see #getSyntaxNode()
	 * @see ASTNode#getParent()
	 */
	public void retrieveContainerFromHierarchy();
	
	/**
	 * Creates a class belonging to the package in the code
	 * @param newClass
	 * Class to create
	 * @return Created class handler
	 */
	public IClassService<?, ?> createClass(Class newClass);
	
	/**
	 * Creates a nested package in the code
	 * @param newPackage
	 * Nested package to create
	 * @return Created nested package handler
	 */
	public IPackageService createNestedPackage(Package newPackage);
	
	/**
	 * Creates a nested package in the code
	 * @param PackageName
	 * Name of the nested package to create
	 * @return Created nested package handler
	 */
	public IPackageService createNestedPackage(String packageName);
	
	/**
	 * Removes a class from the code
	 * @param oldClass
	 * Class to remove
	 */
	public void removeClass(Class oldClass);
	
	/**
	 * Removes a nested package in the code
	 * @param oldPackage
	 * Nested package to remove
	 */
	public void removeNestedPackage(Package oldPackage);
}