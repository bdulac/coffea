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
	 * Builds an EMF URI for a the managed <em>Java</em> project
	 * @return EMF URI for the given model in project
	 * @see #getJavaProject()
	 */
	public URI createEmfUri();
	
	/**
	 * @return The Java project URI string
	 * @see #getJavaProject()
	 */
	public String getJavaProjectUriString();

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
	
	/** Creates a file for the model underlying the service */
	public void createModelFile();
	
	/**
	 * Creates a file for the model underlying the service with a progression 
	 * monitor
	 * @param monitor
	 * Progress monitor
	 */
	public void createModelFile(IProgressMonitor monitor);
	
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