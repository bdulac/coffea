package net.sourceforge.coffea.uml2tools.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import net.sourceforge.coffea.uml2.model.IElementService;

public class ElementServiceResourceSetListener implements ResourceSetListener {
	
	private IElementService elementSrv;
	
	/** 
	 * Map of diagram editor in which the element is being edited and 
	 * corresponding EMF editing domains 
	 */
	protected Map<UMLClassDiagramJavaEditor, TransactionalEditingDomain> editingDomains;
	
	public ElementServiceResourceSetListener(IElementService srv) {
		if(srv == null)throw new NullPointerException();
		elementSrv = srv;
		editingDomains = new HashMap<UMLClassDiagramJavaEditor, TransactionalEditingDomain>();
	}
	
	

	@Override
	public NotificationFilter getFilter() {
		return null;
	}

	@Override
	public boolean isAggregatePrecommitListener() {
		return false;
	}

	@Override
	public boolean isPostcommitOnly() {
		return false;
	}

	@Override
	public boolean isPrecommitOnly() {
		return false;
	}

	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
	}

	@Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		return null;
	}
	
	public String toString() {
		return super.toString() + '#' + elementSrv.getFullName();
	}
}