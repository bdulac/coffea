package net.sourceforge.coffea.edit;

import net.sourceforge.coffea.editors.figures.OperationFigure;
import net.sourceforge.coffea.uml2.model.IMethodService;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class OperationEditPart extends AbstractGraphicalEditPart implements EditPart {

	protected IMethodService model;
	
	public OperationEditPart(IMethodService m) {
		model = m;
	}
	
	@Override
	protected IFigure createFigure() {
		return new OperationFigure(model);
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

}
