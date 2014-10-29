package net.sourceforge.coffea.uml2.model;


import net.sourceforge.coffea.uml2.Resources;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
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
	 * Builds an EMF URI for a the managed <em>Java</em> element
	 * @return EMF URI for the given model in project
	 * @see #getJavaElement()
	 */
	public URI createEmfUri();
	
	/**
	 * @return The Java project URI string
	 * @see #getJavaElement()
	 */
	public String getJavaElementUriString();

	/** @return <em>Java</em> element underlying the model */
	public IJavaElement getJavaElement();
	
	/**
	 * Sets the <em>Java</em> element underlying the model handled by the 
	 * service
	 * @param jElement 
	 * <em>Java</em> element underlying the model
	 */
	public void setJavaElement(IJavaElement jElement);
	
	/** 
	 * @return First parent of the underlying Java element being a package 
	 * fragment 
	 */
	public IPackageFragment getFirstPackageFragment();
	
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
	
	/** Disposal of all resources managed by the service */
	public void dispose();

	/** @return EMF resource to which the model belongs */
	public Resource getEmfResource();
}