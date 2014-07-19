package net.sourceforge.coffea.editors.actiondelegates;

import net.sourceforge.coffea.editors.JavaElementsReverseReceiver;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;

/** 
 * Reverse the selected element's package to an UML class diagram and opens 
 * this diagram in an editor 
 */
public class ReverseActionDelegate implements IObjectActionDelegate {

	/** Workbench window in which the edition action is proceeded */
	protected IWorkbenchWindow window;
	
	/** Current selection */
	protected ISelection selection;
	
	/** Receiver handling the interactions */
	private JavaElementsReverseReceiver interactionsReceiver;
	
	/** Action delegate */
	public ReverseActionDelegate() {
		super();
		interactionsReceiver = new JavaElementsReverseReceiver();
	}
	
	public void dispose() {

	}

	public void init(IWorkbenchWindow w) {
		window = w;
	}

	public void run(IAction act) {
		if(window!=null) {
			try {
				interactionsReceiver.reverseFromJavaElements(window);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void selectionChanged(IAction act, ISelection sel) {
		selection = sel;
	}

	public void setActivePart(IAction act, IWorkbenchPart part) {
		if(part!=null) {
			IWorkbenchPartSite site = part.getSite();
			if(site!=null) {
				window = site.getWorkbenchWindow();
			}
		}
	}

}