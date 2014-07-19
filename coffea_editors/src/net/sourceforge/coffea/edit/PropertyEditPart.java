package net.sourceforge.coffea.edit;

import net.sourceforge.coffea.editors.figures.PropertyFigure;
import net.sourceforge.coffea.uml2.model.IAttributeService;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class PropertyEditPart extends AbstractGraphicalEditPart implements EditPart {

	protected IAttributeService model;
	
	public PropertyEditPart(IAttributeService m) {
		model = m;
	}
	
	@Override
	protected IFigure createFigure() {
		return new PropertyFigure(model);
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

}
