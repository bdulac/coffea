package net.sourceforge.coffea.edit.policy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public abstract class ClassifierNodeEditPolicy<I extends EditPart> 
extends GraphicalNodeEditPolicy 
implements EditPolicy {

	private I host;
	
	protected ClassifierNodeEditPolicy(I h) {
		host = h;
	}

	@Override
	public I getHost() {
		return host;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setHost(EditPart editPart) {
		host = (I)editPart;
		
	}
	
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		return null;
	}

	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request
	) {
		return null;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}