package net.sourceforge.coffea.edit.policy;

import net.sourceforge.coffea.edit.InterfaceEditPart;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

public class InterfaceDirectEditPolicy<I extends InterfaceEditPart<?>> extends DirectEditPolicy {

	private I host;
	
	protected InterfaceDirectEditPolicy(I h) {
		host = h;
	}
	
	@Override
	public I getHost() {
		return host;
	}
	
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		getHost().getModel();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		// TODO Auto-generated method stub
	}

}
