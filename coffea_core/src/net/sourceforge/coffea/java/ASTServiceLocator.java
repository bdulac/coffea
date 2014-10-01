package net.sourceforge.coffea.java;

import java.io.File;

import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.creation.ModelServiceBuilder;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Service locator using elements of the AST to manage UML model services
 * @see AST
 */
public class ASTServiceLocator extends JavaModelServiceLocator {

	/** Full reverse handler construction */
	public ASTServiceLocator() {
	}
	
	protected IWorkbenchWindow getSourceWorkbenchWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win;
	}
	
	/** 
	 * Creation of an UML model service from the active selection in a workbench : uses an 
	 * AST to produce the service
	 * @param workbench
	 * Workbench window
	 * @return Model service
	 * @throws ExecutionException
	 */
	@Override
	public IModelService getModelService(IWorkbenchWindow workbench)
	throws ExecutionException {
		lastSourceWorkbenchWindow = workbench;
		String sourceViewId = 
			getSourceViewIdForWorkbench(workbench);
		lastSourceViewId = sourceViewId;
		File target = null;
		ITreeSelection treeSel = 
			getTreeSelectionFromWorbench(workbench, sourceViewId);
		// If we have a selection, 
		if(treeSel != null) {
			IProject proj = null;
			// Then we try to get a selected Java element
			IJavaElement el = getJavaElementFromSelection(treeSel);
			// If a Java element is selected, 
			if(el != null) {
				// Then we get the file system path to the workspace
				String path = 
					ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString();
				// Then the path to the Java element
				path += el.getPath().toOSString();
				target = new File(path);
				IJavaProject proJ = el.getJavaProject();
				if(proJ != null) {
					proj = proJ.getProject();
				}
			}
			if(proj == null) {
				proj = selectedProject(treeSel);
			}
			// Once we have an adequate form of the selection
			if(proj!=null) {
				// We proceed reversing
				if(target==null) {
					// (using the project location as a default target)
					target = 
						new File(
								proj.getLocation().toOSString()
						);
				}
				IWorkbenchWindow win = getSourceWorkbenchWindow();
				ModelServiceBuilder worker = 
					new ModelServiceBuilder(sourceViewId, win);
				worker.setCoffeeName(proj.getName());
				IModelService model =
					worker.parseFile(target);
				// Save the reversed model in the file system
				worker.save(
						target.getPath(), 
						worker.getCoffeeName()
				);
				workbench = null;
				return model;
			}
		}
		workbench = null;
		return null;
	}
}