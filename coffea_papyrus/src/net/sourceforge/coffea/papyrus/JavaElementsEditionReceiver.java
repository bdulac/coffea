package net.sourceforge.coffea.papyrus;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.java.JavaElementsReverseReceiver;
import net.sourceforge.coffea.papyrus.diagram.creation.ClassDiagramBuilder;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Receives reverse engineering actions on  
 * {@link org.eclipse.jdt.core.IJavaElement <em>Java</em> elements}, common 
 * resources for actions and handlers
 * @see org.eclipse.jdt.core.IJavaElement
 */
public class JavaElementsEditionReceiver 
extends JavaElementsReverseReceiver {

	/** Java element object of the action */
	protected IJavaElement element;

	/** Produced model */
	protected IModelService model;

	/** 
	 * Edition of a <em>Java</em> element using an UML class diagram 
	 * representation
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
	public IModelService edit(
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
	 * Edition of the current selection using an UML class diagram 
	 * representation
	 * @param workbenchWindow
	 * Workbench window in which the current selection must be reversed
	 * @param sourceViewId
	 * Source view identifier in the source workbench window
	 * @return Operation result
	 * @throws ExecutionException
	 */
	public IModelService editSelectedJavaElements(IWorkbenchWindow workbenchWindow) 
	throws ExecutionException { 
		String sourceViewId = 
			fetchSourceViewId(
					workbenchWindow
			);
		IJavaElement el = getSelectedJavaElement(workbenchWindow);
		if(el != null) {
			return edit(
					el, 
					workbenchWindow, 
					sourceViewId
			);
		}
		workbenchWindow = null;
		return null;
	}

	/** 
	 * Edition of the current selection using an UML class diagram
	 * @param edition
	 * Boolean value indicating if the source should be edited through the 
	 * UML model editor
	 * @param workbenchWindow
	 * Workbench window in which the current selection must be reversed
	 * @param sourceViewId
	 * Source view identifier in the source workbench window
	 * @return Operation result
	 * @throws ExecutionException
	 */
	public Object editSelectedJavaElement(
			IWorkbenchWindow workbenchWindow, 
			String sourceViewId
	) throws ExecutionException { 
		ITreeSelection treeSel = 
			fetchTreeSelection(workbenchWindow, sourceViewId);
		// If we have a selection, 
		if(treeSel!=null) {
			IJavaElement el = selectedJavaElement(treeSel);
			return edit(
					el, 
					workbenchWindow, 
					sourceViewId
			);
		}
		workbenchWindow = null;
		return null;
	}

	/** Simple reverse action construction */
	public JavaElementsEditionReceiver() {
	}

	protected IWorkbenchWindow getSourceWorkbenchWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win;
	}
	
	/** 
	 * Edition of the current selection using an UML class diagram 
	 * representation
	 * @param w
	 * Workbench window in which the reverse is done
	 * @return Result of the operation
	 * @throws ExecutionException
	 */
	protected Object editCurrentSelection(IWorkbenchWindow w)
	throws ExecutionException {
		lastSourceWorkbenchWindow = w;
		String sourceViewId = fetchSourceViewId(w);
		lastSourceViewId = sourceViewId;
		ITreeSelection treeSel = fetchTreeSelection(
				w, 
				sourceViewId
		);
		// If we have a selection, 
		if(treeSel!=null) {
			IJavaElement el = selectedJavaElement(treeSel);
			return edit(
					el, 
					w, 
					sourceViewId
			);
		}
		w = null;
		return null;
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
			IWorkbenchWindow win = getSourceWorkbenchWindow();
			// We will be able to build a model
			ClassDiagramBuilder builder = 
				new ClassDiagramBuilder(lastSourceViewId, win);
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

}