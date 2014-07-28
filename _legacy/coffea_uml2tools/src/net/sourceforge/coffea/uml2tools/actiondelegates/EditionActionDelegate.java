package net.sourceforge.coffea.uml2tools.actiondelegates;

import net.sourceforge.coffea.editors.actiondelegates.ReverseActionDelegate;
import net.sourceforge.coffea.uml2tools.JavaElementsEditionReceiver;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;

/** 
 * Edit the selected element's package in an UML class diagram editor; any 
 * change done to the package UML model will be propagated to the source code.
 */
public class EditionActionDelegate extends ReverseActionDelegate {
	
	/** Receiver handling the interactions */
	private JavaElementsEditionReceiver interactionsReceiver;
	
	/** Action delegate construction */
	public EditionActionDelegate() {
		super();
		interactionsReceiver = new JavaElementsEditionReceiver();
	}
	
	@Override
	public void run(IAction act) {
		if(window!=null) {
			try {
				interactionsReceiver.editSelectedJavaElements(window);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
}
