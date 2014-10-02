package net.sourceforge.coffea.uml2.model;


import net.sourceforge.coffea.uml2.Resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;

/** Service for a model */
public interface IModelService extends 
IPackagesGroupService, 
ITypesContainerService {

	/** Name of the primitive types package */
	public static final String primitiveTypesPackageName = 
		Resources.getParameter("constants.primitiveTypesPackageName");
	
	/** Name of the class path package */
	public static final String classpathTypesPackageName = 
		Resources.getParameter("constants.classpathPackageName");
	
	/** Default package name */
	public static final String defaultPackageFileName = 
		Resources.getParameter("constants.defaultPackageFileName");
	
	public Model getUMLElement();
	
	/**
	 * Builds an EMF URI for a model given its name and the directory URI as a 
	 * string
	 * @param uri
	 * Containing directory URI as a string
	 * @return EMF URI for the given model in the given directory
	 */
	public URI createEmfUri(String uri);

	/**
	 * Returns the <em>Java</em> project containing the model handled by the 
	 * service
	 * @return <em>Java</em> project containing the model
	 */
	public IJavaProject getJavaProject();
	
	/**
	 * Sets the <em>Java</em> project containing the model handled by the 
	 * service
	 * @param p 
	 * <em>Java</em> project containing the model
	 */
	public void setJavaProject(IJavaProject p);
	
	/**
	 * Sets the model name
	 * @param n
	 * Model name
	 */
	public void setName(String n);
	
	/**
	 * Creates a file for the model under the given resource identifier
	 * @param uri
	 * Target resource identifier
	 */
	public void createModelFile(String uri);
	
	/**
	 * Creates a file for the model under the given resource identifier and 
	 * monitors the creation
	 * @param uri
	 * Target resource identifier
	 * @param monitor
	 * Progress monitor
	 */
	public void createModelFile(String uri, IProgressMonitor monitor);
	
	/**
	 * Returns a boolean value indicating if the packages form a hierarchy in 
	 * the model
	 * @return Boolean value indicating if the packages for a hierarchy in the 
	 * model ({@code false} if all packages are considered equal) 
	 */
	public boolean arePackageInHierarchy();
	
	/**
	 * @return The class diagram EMF resource
	 */
	public IResource getClassDiagramEMFResource();
	
	/**
	 * @return The class diagram workspace resource
	 */
	public IResource getClassDiagramWorkspaceResource();
}