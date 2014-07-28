package net.sourceforge.coffea.editors.figures;

import net.sourceforge.coffea.edit.Icons;
import net.sourceforge.coffea.uml2.model.IAttributeService;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.VisibilityKind;

public class PropertyFigure extends Figure {
	
	private IAttributeService model;
	
	public PropertyFigure(IAttributeService m) {
		setLayoutManager(new FlowLayout(true));
		model = m;
		model.setUpUMLModelElement();
		// String visibility = new String();
		Image icon = null;
		if(
				model.getUMLElement().getVisibility().equals(
						VisibilityKind.PRIVATE_LITERAL
				)
		) {
			// visibility = "-";
			icon = Icons.createPrivatePropertyIcon();
		}
		else if(
				model.getUMLElement().getVisibility().equals(
						VisibilityKind.PROTECTED_LITERAL
				)
		) {
			// visibility = "#";
			icon = Icons.createProtectedPropertyIcon();
		}
		else if(
				model.getUMLElement().getVisibility().equals(
						VisibilityKind.PUBLIC_LITERAL
				)
		) {
			// visibility = "+";
			icon = Icons.createPublicPropertyIcon();
		}
		else {
			icon = Icons.createDefaultPropertyIcon();
		}
		String name = model.getSimpleName();
		Label propertyLabel = 
			new Label(name, icon);
		add(propertyLabel);
	}

}
