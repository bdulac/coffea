package net.sourceforge.coffea.edit;

import net.sourceforge.coffea.edit.policy.ClassNodeEditPolicy;
import net.sourceforge.coffea.editors.figures.ClassFigure;
import net.sourceforge.coffea.uml2.model.IClassService;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

public class ClassEditPart extends InterfaceEditPart<IClassService<?, ?>> {

	
	public ClassEditPart(IClassService<?, ?> m) {
		super(m);
	}

	@Override
	protected IFigure createFigure() {
		return new ClassFigure(model);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ClassNodeEditPolicy(this));
	}
}