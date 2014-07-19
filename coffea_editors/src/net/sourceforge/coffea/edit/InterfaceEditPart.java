package net.sourceforge.coffea.edit;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.edit.policy.InterfaceNodeEditPolicy;
import net.sourceforge.coffea.editors.figures.InterfaceFigure;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class InterfaceEditPart<I extends IInterfaceService<?, ?>> 
extends AbstractGraphicalEditPart implements EditPart, NodeEditPart {

	protected I model;
	
	public InterfaceEditPart(I m) {
		model = m;
	}
	
	public I getModel() {
		return model;
	}

	@Override
	protected IFigure createFigure() {
		return new InterfaceFigure<I>(model);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(
				EditPolicy.GRAPHICAL_NODE_ROLE, 
				new InterfaceNodeEditPolicy< InterfaceEditPart<I>>(this)
		);
	}
	
	@Override
	protected List<?> getModelChildren() {
		List<IElementService> l = new ArrayList<IElementService>();
		for(IElementService e : model.getElementsHandlers()) {
			l.add(e);
		}
		return l;
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection
	) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection
	) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// TODO Auto-generated method stub
		return null;
	}
}