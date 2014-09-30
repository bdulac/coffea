package net.sourceforge.coffea.java;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.creation.ModelServiceBuilder;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Service locator using elements of the Java model to manage UML model services
 * @see IJavaModel
 * @see IJavaElement
 */
public class JavaModelServiceLocator implements IRunnableWithProgress {
	
	/**
	 * Returns the selected <em>Java</em> element (only if it is in first 
	 * position in the selection)
	 * @param treeSel
	 * Selection
	 * @return Selected <em>Java</em> element
	 */
	protected static IJavaElement getJavaElementFromSelection(ITreeSelection treeSel) {
		IJavaElement el = null;
		if(treeSel!=null) {
			Object first = treeSel.getFirstElement();
			if(first instanceof IJavaElement) {
				el = (IJavaElement)first;
			}
		}
		return el;
	}

	protected static IJavaElement getSelectedJavaElement(IWorkbenchWindow workbenchWindow)  
			throws ExecutionException  {
		String sourceViewId = getSourceViewIdForWorkbench(workbenchWindow);
		ITreeSelection treeSel = 
			getTreeSelectionFromWorbench(workbenchWindow, sourceViewId);
		// If we have a selection, 
		if(treeSel!=null) {
			IJavaElement el = getJavaElementFromSelection(treeSel);
			return el;
		}
		return null;
	}

	protected static String getSourceViewIdForWorkbench(
			IWorkbenchWindow workbenchWindow
	) {
		String sourceViewId = null;
		if(workbenchWindow!=null) {
			IWorkbenchPage[] pages = workbenchWindow.getPages();
			if(pages!=null) {
				// For each of these pages...
				IWorkbenchPage page = null;
				//ISelection selection = null;
				for(int i=0 ; i<pages.length ; i++) {
					page = pages[i];
					IWorkbenchPartReference activePart = 
						page.getActivePartReference();
					if(activePart instanceof IViewReference) {
						IViewReference viewPart =(IViewReference)activePart;
						sourceViewId = viewPart.getId();
					}
				}
			}
		}
		return sourceViewId;
	}

	/**
	 * Fetches the current tree selection the source workbench window
	 * @param workbenchWindow
	 * Source workbench window
	 * @param sourceViewId
	 * Source view identifier in the source workbench window
	 * @return Current tree selection
	 * @throws ExecutionException
	 */
	protected static ITreeSelection getTreeSelectionFromWorbench(
			IWorkbenchWindow workbenchWindow, 
			String sourceViewId
	) 
	throws ExecutionException {
		ISelection selection = null;
		ITreeSelection treeSel = null;
		if((workbenchWindow!=null)&&(sourceViewId!=null)) {
			// Accessing the workbench pages
			IWorkbenchPage[] pages = workbenchWindow.getPages();
			if(pages!=null) {
				// For each of these pages...
				IWorkbenchPage page = null;
				for(int i=0 ; i<pages.length ; i++) {
					page = pages[i];
					// We try to get the selection in the source view
					if(page!=null) {
						selection = page.getSelection(sourceViewId);
					}
				}
				// If we have a selection, 
				if(selection!=null) {
					// We try to specialize it
					if(selection instanceof ITreeSelection) {
						treeSel = (ITreeSelection)selection;
					}
				}	
			}
		}
		return treeSel;
	}

	/** Last source workbench window */
	protected IWorkbenchWindow lastSourceWorkbenchWindow;

	/** Last source view identifier in the last source workbench window */
	protected String lastSourceViewId;

	/** Java element object of the action */
	protected IJavaElement element;

	/** Produced model */
	protected IModelService model;

	/** Simple reverse action construction */
	public JavaModelServiceLocator() {
	}
	
	/**
	 * Returns Last source workbench window
	 * @return Last source workbench window
	 */
	public IWorkbenchWindow getLastSourceWorkbenchWindow() {
		return lastSourceWorkbenchWindow;
	}

	/**
	 * Returns last source view identifier in the last source workbench window
	 * @return Last source view identifier in the last source workbench window
	 */
	public String getLastSourceViewId() {
		return lastSourceViewId;
	}
	
	/** 
	 * Simple reverse of a <em>Java</em> element to an UML model
	 * @param edition
	 * Boolean value indicating if the source should be edited through the 
	 * UML model editor
	 * @param el
	 * <em>Java</em> element to reverse
	 * @param sourceWorkbenchWindow
	 * Source workbench window from which the operation is triggered
	 * @param sourceViewId
	 * Source view identifier in the source workbench window
	 * @return Produced UML model
	 */
	private IModelService getModelService(
			IJavaElement el, 
			IWorkbenchWindow sourceWorkbenchWindow, 
			String sourceViewId
	) {
		lastSourceWorkbenchWindow = sourceWorkbenchWindow;
		lastSourceViewId = sourceViewId;
		element = el;
		ProgressMonitorDialog dialog = 
			new ProgressMonitorDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell()
			);
		try {
			dialog.run(false, true, this);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return model;
	}

	/** 
	 * Simple reverse of the current selection to an UML model
	 * @param w
	 * Workbench window in which the selection should be reversed
	 * @return Result of the operation
	 * @throws ExecutionException
	 */
	/*
	protected IModelService getModelService(IWorkbenchWindow w)
	throws ExecutionException {
		lastSourceWorkbenchWindow = w;
		String sourceViewId = getSourceViewIdForWorkbench(w);
		lastSourceViewId = sourceViewId;
		ITreeSelection treeSel = getTreeSelectionFromWorbench(
				w, 
				sourceViewId
		);
		// If we have a selection, 
		if(treeSel!=null) {
			IJavaElement el = getJavaElementFromSelection(treeSel);
			return getModelService(
					el, 
					w, 
					sourceViewId
			);
		}
		w = null;
		return null;
	}*/

	protected IProject selectedProject(ITreeSelection treeSel) {
		// We get the first selected element (we consider only one 
		// element can be reversed)
		Object first = treeSel.getFirstElement();
		IProject proj = null;
		// From the element, we can get the underlying project
		if(first instanceof IProject) {
			proj = (IProject)first;
		}
		else if(first instanceof IProjectNature) {
			proj = ((IProjectNature)first).getProject();
		}
		return proj;
	}

	public void run(IProgressMonitor monitor) 
	throws InvocationTargetException, InterruptedException {
		if(monitor==null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask(
				Resources.getMessage("labels.processingSelection"), 
				10
		);
		if(element instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot r = (IPackageFragmentRoot)element;
			try {
				if(r.getKind()==IPackageFragmentRoot.K_SOURCE) {
					element = r.getPackageFragment(new String());
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		// We have to find the nearest package
		while((element != null) && (!(element instanceof IPackageFragment))) {
			element = element.getParent();
		}
		// Once we have a package to process
		if(element != null) {
			IWorkbenchWindow win = getLastSourceWorkbenchWindow();
			// We will be able to build a model
			ModelServiceBuilder builder = 
				new ModelServiceBuilder(lastSourceViewId, win);
			// We can start building the model using a sub-progress monitor
			model = 
				builder.buildModelService(
						element, 
						new SubProgressMonitor(monitor, 7)
				);
			// We get the package location to have the absolute URL
			IPath res = element.getResource().getLocation();
			String path = res.toOSString();
			// We save documents from the processed elements to the package 
			// URL
			builder.save(
					path, 
					builder.getCoffeeName(), 
					new SubProgressMonitor(monitor, 3)
			);
			lastSourceWorkbenchWindow = null;
		}
	}
	
	/** 
	 * Creation of an UML model service from the active selection in a workbench : uses the 
	 * Java model to produce the service
	 * @param workbench
	 * Workbench window
	 * @return Model service
	 * @throws ExecutionException
	 */
	public IModelService getModelService(IWorkbenchWindow workbench) 
	throws ExecutionException { 
		String sourceViewId = getSourceViewIdForWorkbench(workbench);
		IJavaElement el = getSelectedJavaElement(workbench);
		if(el != null) {
			return getModelService(
					el, 
					workbench, 
					sourceViewId
			);
		}
		workbench = null;
		return null;
	}

	/** 
	 * Simple reverse of the current selection to an UML model
	 * @param edition
	 * Boolean value indicating if the source should be edited through the 
	 * UML model editor
	 * @param workbenchWindow
	 * Workbench window in which the current selection must be reversed
	 * @param sourceViewId
	 * Source view identifier in the source workbench window
	 * @return Service for action on the model
	 * @throws ExecutionException
	 */
	/*
	public IModelService getModelForWorkbench(
			IWorkbenchWindow workbenchWindow, 
			String sourceViewId
	) throws ExecutionException { 
		ITreeSelection treeSel = 
			getTreeSelectionFromWorbench(workbenchWindow, sourceViewId);
		// If we have a selection, 
		if(treeSel!=null) {
			IJavaElement el = getJavaElementFromSelection(treeSel);
			return getModelService(
					el, 
					workbenchWindow, 
					sourceViewId
			);
		}
		workbenchWindow = null;
		return null;
	}*/
}