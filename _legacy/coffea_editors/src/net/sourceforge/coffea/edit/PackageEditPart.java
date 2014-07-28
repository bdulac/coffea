package net.sourceforge.coffea.edit;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.editors.figures.PackageFigure;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class PackageEditPart<P extends IPackageService> 
extends AbstractGraphicalEditPart implements EditPart {

	protected P model;
	
	public PackageEditPart(P m) {
		model = m;
	}

	@Override
	protected IFigure createFigure() {
		return new PackageFigure<P>(model);
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<?> getModelChildren() {
		List<IElementService> l = new ArrayList<IElementService>();
		for(IElementService e : model.getElementsHandlers()) {
			l.add(e);
		}
		return l;
	  }
}