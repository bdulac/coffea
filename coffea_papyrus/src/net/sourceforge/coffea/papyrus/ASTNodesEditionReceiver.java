package net.sourceforge.coffea.papyrus;

import java.io.File;

import net.sourceforge.coffea.java.ASTNodesReverseReceiver;
import net.sourceforge.coffea.papyrus.diagram.creation.ClassDiagramBuilder;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Receives reverse engineering actions on 
 * {@link org.eclipse.jdt.core.dom.AST AST} nodes, common resources for 
 * actions and handlers
 * @see org.eclipse.jdt.core.dom.AST AST
 */
public class ASTNodesEditionReceiver 
extends ASTNodesReverseReceiver {

	/** Full reverse handler construction */
	public ASTNodesEditionReceiver() {
	}
	
	/** 
	 * Full reverse to an UML model : uses an AST to produce the model
	 * @param edition
	 * @param event
	 * Event triggering the operation
	 * @return Result of the operation
	 * @throws ExecutionException
	 */
	public Object editFromASTNodes(IWorkbenchWindow workbenchWindow)
	throws ExecutionException {
		lastSourceWorkbenchWindow = workbenchWindow;
		String sourceViewId = 
			fetchSourceViewId(workbenchWindow);
		lastSourceViewId = sourceViewId;
		File target = null;
		ITreeSelection treeSel = 
			fetchTreeSelection(workbenchWindow, sourceViewId);
		// If we have a selection, 
		if(treeSel!=null) {
			IProject proj = null;
			// Then we try to get a selected Java element
			IJavaElement el = selectedJavaElement(treeSel);
			// If a Java element is selected, 
			if(el!=null) {
				// Then we get the file system path to the workspace
				String path = 
					ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString();
				// Then the path to the Java element
				path += el.getPath().toOSString();
				target = new File(path);
				IJavaProject proJ = el.getJavaProject();
				if(proJ!=null) {
					proj = proJ.getProject();
				}
			}
			if(proj==null) {
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
				ClassDiagramBuilder worker = 
					new ClassDiagramBuilder(sourceViewId, win);
				// worker.setCoffeeName(proj.getName());
				IModelService model =
					worker.buildModelService(proj.getLocation().toOSString());
				// Save the reversed model in the file system
				worker.save(
						target.getPath(), 
						worker.getCoffeeName()
				);
				workbenchWindow = null;
				return model;
			}
		}
		workbenchWindow = null;
		return null;
	}
}
