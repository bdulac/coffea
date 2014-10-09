package net.sourceforge.coffea.uml2.model.creation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.model.impl.ModelService;

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

	/** Handled model from which the UML model is constituted */
	private ModelService modelService;

	public CreateModelRunnable(ModelService m) {
		if(m == null)throw new NullPointerException();
		modelService = m;
	}

	public void run(IProgressMonitor monitor)
	throws InvocationTargetException, InterruptedException {
		createModelFile(monitor);
	}

	protected void createModelFile(IProgressMonitor monitor) {
		monitor.beginTask("labels.buildingModelResources", 10);	
		// Setting up the model location
		URI location = modelService.createEmfUri();
		ResourceSet set = new ResourceSetImpl();
		// Saving the model resource
		Resource emfResource = set.createResource(location);
		monitor.worked(2);
		// Getting the model element
		Model model = modelService.getUMLElement();
		if(model == null) {
			throw new IllegalStateException(
					"The UML model should not be null"
			);
		}
		emfResource.getContents().add(model);
		try {
			emfResource.save(null);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.print(ioe.getMessage());
		}
		monitor.worked(2);
		String modelPath = modelService.getJavaElementUriString();
		modelPath = 
			modelPath.substring(
					ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString().length()
			);
		try {
			IResource resultingWorkspaceResource = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(
						new Path(modelPath)
				);
			resultingWorkspaceResource.refreshLocal(
					1, 
					monitor
			);
			modelService.setEmfRefource(emfResource);
			modelService.dispose();
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
}