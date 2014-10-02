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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;

/** Runnable creating an UML model from a handled model */
public class CreateModelRunnable implements IUML2RunnableWithProgress {

	private String targetDirUri;

	/** Handled model from which the UML model is constituted */
	private IModelService modelService;

	/** Model EMF resource in which the UML model persists */
	protected Resource resultingEmfResource;

	/** Model workspace resource corresponding to the UML model */
	protected IResource resultingWorkspaceResource;

	public CreateModelRunnable(String dirUri, IModelService m) {
		targetDirUri = dirUri;
		modelService = m;
	}
	
	protected String getUri() {
		return targetDirUri;
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
			
		// Setting up the model location
		URI location = modelService.createEmfUri(targetDirUri);
		ResourceSet set = new ResourceSetImpl();
		// Saving the model resource
		resultingEmfResource = set.createResource(location);
		monitor.worked(2);
		// Getting the model element
		Model model = modelService.getUMLElement();
		if(model == null) {
			throw new IllegalStateException(
					"The UML model should not be null"
			);
		}
		resultingEmfResource.getContents().add(model);
		try {
			resultingEmfResource.save(null);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.print(ioe.getMessage());
		}
		monitor.worked(2);
		// String modelPath = buildUMLModelPath(uri, m);
		String modelPath = targetDirUri;
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