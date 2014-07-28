package net.sourceforge.coffea.edit;

import net.sourceforge.coffea.uml2.model.IAttributeService;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.IPackageService;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class DiagramEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if(model instanceof IClassService) {
			part = new ClassEditPart((IClassService<?, ?>)model);
		}
		else if (model instanceof IInterfaceService) {
			part = new InterfaceEditPart<IInterfaceService<?, ?>>((IInterfaceService<?, ?>)model);
		}
		else if (model instanceof IPackageService) {
			part = new PackageEditPart<IPackageService>((IPackageService)model);
		}
		else if (model instanceof IMethodService) {
			part = new OperationEditPart((IMethodService)model);
		}
		else if (model instanceof IAttributeService) {
			part = new PropertyEditPart((IAttributeService)model);
		}
		return part;
	}

}
