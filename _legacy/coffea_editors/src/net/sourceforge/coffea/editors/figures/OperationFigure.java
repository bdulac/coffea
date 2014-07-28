package net.sourceforge.coffea.editors.figures;

import net.sourceforge.coffea.edit.Icons;
import net.sourceforge.coffea.editors.CoffeaEditorsPlugin;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.ITypeService;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.VisibilityKind;

public class OperationFigure extends Figure {

	private IMethodService model;
	
	public OperationFigure(IMethodService m) {
		super();
		setLayoutManager(new FlowLayout(true));
		model = m;
		model.setUpUMLModelElement();
		Image icon = null;
		// String visibility = new String();
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
		String name = model.getSimpleName();
		ITypeService<?, ?> returnTypeSrv = 
			model.getReturnTypeHandler();
		// String value = visibility + ' ' +name;
		String value = name;
		if(returnTypeSrv != null) {
			value += " : " + returnTypeSrv.getSimpleName();
		}
		else {
			try {
				value += " : " + model.getJavaElement().getReturnType();
			} catch (JavaModelException e) {
				CoffeaEditorsPlugin.getDefault().logError(
						e.getMessage(), 
						e
				);
			}
		}
		Label operationLabel = 
			new Label(value, icon);
		add(operationLabel);
	}
	
}
