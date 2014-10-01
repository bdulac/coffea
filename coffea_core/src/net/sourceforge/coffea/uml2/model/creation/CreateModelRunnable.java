package net.sourceforge.coffea.uml2.model.creation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.resource.UMLResource;

/** Runnable creating an UML model from a handled model */
public class CreateModelRunnable implements IUML2RunnableWithProgress {

	private String uri;

	/** Handled model from which the UML model is constituted */
	private IModelService model;

	/** Model EMF resource in which the UML model persists */
	protected Resource resultingEmfResource;

	/** Model workspace resource corresponding to the UML model */
	protected IResource resultingWorkspaceResource;

	public CreateModelRunnable(String uri, IModelService m) {
		this.uri = uri;
		model = m;
	}
	
	protected String getUri() {
		return uri;
	}

	protected URI setupEMFLocation(String uri, String modelName) {
		return 
		URI.createURI(
				"file://" + uri
		).appendSegment(modelName).appendFileExtension(
				UMLResource.FILE_EXTENSION
		);
	}

	public void run(IProgressMonitor monitor)
	throws InvocationTargetException, InterruptedException {
		createModel(monitor);
		disposeResources(monitor);
	}

	/** 
	 * Returns the workspace resource created for the model, relevant only 
	 * after the runnable execution
	 * @return Workspace resource created for the model
	 */
	public IResource getResultingWorkspaceResource() {
		return resultingWorkspaceResource;
	}

	/** 
	 * Returns the workspace resource created for the model, relevant only 
	 * after the runnable execution
	 * @return Workspace resource created for the model
	 */
	public Resource getResultingEMFResource() {
		return resultingEmfResource;
	}

	protected void createModel(IProgressMonitor monitor) {
		monitor.beginTask("labels.buildingModelResources", 10);
		// Getting the model element
		Model m = model.getUMLElement();
		if((m!=null)&&(m.getName()!=null)) {
			//Setting up the model location
			URI location = buildEMFModelURI(uri, m);
			// Saving the model resource
			resultingEmfResource = 
				new ResourceSetImpl().createResource(location);
			monitor.worked(2);
			resultingEmfResource.getContents().add(m);
			try {
				resultingEmfResource.save(null);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.err.print(ioe.getMessage());
			}
			monitor.worked(2);
			// String modelPath = buildUMLModelPath(uri, m);
			String modelPath = uri;
			modelPath = 
				modelPath.substring(
						ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toOSString().length()
				);
			try {
				resultingWorkspaceResource = 
					ResourcesPlugin.getWorkspace().getRoot().findMember(
							new Path(modelPath)
					);
				resultingWorkspaceResource.refreshLocal(
						1, 
						monitor
				);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			/*
				if(creator.isEditing()) {
					modelPath = 
						uri 
						+ '/' 
						+ 
						Resources.getParameter(
								"constants.editingFileNamePrefix"
						)
						+ m.getName() + ".uml";
				}
				else {
					modelPath = uri + '/' + m.getName() + ".uml";
				}
			 */
			/*
				modelPath = 
					modelPath.substring(
							ResourcesPlugin.getWorkspace().getRoot()
							.getLocation().toOSString().length()
					);
				modelWorkspaceResource = 
					ResourcesPlugin.getWorkspace().getRoot().findMember(
							new Path(modelPath)
					)
					;
				monitor.worked(2);
			}
			 */
		}
	}

	/**
	 * Builds an EMF URI for a model given its name and the directory URI as a 
	 * string
	 * @param uri
	 * Containing directory URI as a string
	 * @param m
	 * Model for which an EMF URI must be built
	 * @return EMF URI for the given model in the given directory
	 */
	protected URI buildEMFModelURI(String uri, Model m) {
		URI location = setupEMFLocation(uri, m.getName());
		return location;
	}

	/**
	 * Builds a path for a model given its name and the directory URI as a 
	 * string
	 * @param uri
	 * Containing directory URI as a string
	 * @param m
	 * Model for which a path must be built
	 * @return Path as a string for the given model in the given directory
	 */
	protected String buildUMLModelPath(String uri, Model m) {
		String modelPath = uri + '/' + m.getName() + ".uml";
		return modelPath;
	}

	/** 
	 * Builds a default diagram on the model
	 * @param monitor
	 * Progression monitor
	 */
	protected void buildDiagram(
			String diagramDirectoryUri, 
			URI modelLocation, 
			Model m, 
			IProgressMonitor monitor
	) {
		// To be specialized
	}

	protected void disposeResources(IProgressMonitor monitor) {
		resultingEmfResource.unload();
		resultingEmfResource = null;
	}
}