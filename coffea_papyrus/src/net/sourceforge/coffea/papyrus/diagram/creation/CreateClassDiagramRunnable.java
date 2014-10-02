package net.sourceforge.coffea.papyrus.diagram.creation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Model;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.creation.CreateModelRunnable;

public class CreateClassDiagramRunnable extends CreateModelRunnable {

	public CreateClassDiagramRunnable(String uri, IModelService m) {
		super(uri, m);
	}
	
	@Override
	protected String buildUMLModelPath(String uri, Model m) {
		String modelPath = 
			uri 
			+ '/' 
			+ 
			Resources.getParameter(
					"constants.editingFileNamePrefix"
			)
			+ m.getName() + ".uml";
		return modelPath;
	}
	
	private IWorkbenchWindow getSourceWorkbenchWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win;
	}
	
	@Override
	protected void buildDiagram(
			String diagramDirectoryUri, 
			URI modelLocation, 
			Model m, 
			IProgressMonitor monitor
	) {
		String uri = getUri();
		IWorkbenchWindow win = getSourceWorkbenchWindow();
		ClassDiagramBuilder diagBuilder = new ClassDiagramBuilder(uri, win);
				// new ClassDiagramBuilder(modelLocation, uri, m.getName());
		diagBuilder.buildModelService(uri);
		// diagBuilder.build(monitor);
		monitor.worked(4);
		String resourcePath = uri;
		resourcePath = 
				resourcePath.substring(
						ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toOSString().length()
						);
		IResource workspaceResource = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(
						// new Path(resourcePath)
						resourcePath
						);
		monitor.worked(2);
		try {
			workspaceResource.refreshLocal(
					1, 
					monitor
					);
			// diagBuilder.openDiagram();
		}catch (PartInitException ex) {
			CoffeaUML2Plugin.getInstance().logError(
					"Unable to open editor", 
					ex
					);
			CoffeaUML2Plugin.getInstance().logError(
					"Unable to open editor", 
					ex
					);
		} catch (CoreException e) {
			CoffeaUML2Plugin.getInstance().logError(
					"Unable to refresh project " + m.getName(), 
					e
					);
		}
		/*
		ClassDiagramBuilder diagBuilder = 
				new ClassDiagramBuilder(modelLocation, diagramDirectoryUri, m.getName());
				*/
		/*diagBuilder.build(monitor);
		monitor.worked(4);
		*/
	}

}