package net.sourceforge.coffea.editors.figures;

import net.sourceforge.coffea.uml2.model.ITypeService;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Figure displaying a type 
 * @param <T>
 * Type of service for the displayed type
 */
public abstract class TypeFigure<T extends ITypeService<?, ?>> extends Figure {
	
	/** Figure background color */
	protected Color backgroundColor;
	
	/** Service for the displayed type */
	protected T typeService;
	
	/** Type icon */
	protected Image icon;
	
	/**
	 * Construction of a figure displaying a type
	 * @param srv
	 * Service for the displayed type
	 */
	public TypeFigure(T srv) {
		if(srv == null) {
			throw new IllegalArgumentException(
					"Missing type service"
			);
		}
		typeService = srv;
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(4);
		setLayoutManager(layout);	
		setBorder(new LineBorder(ColorConstants.black,1));
		backgroundColor = new Color(null,255,255,255);
		setBackgroundColor(backgroundColor);
		setOpaque(true);
		String name = typeService.getSimpleName();
		Label nameLabel = new Label(name, createTypeIcon());
		add(nameLabel);
	}
	
	/**
	 * Icon creation
	 * @return Created icon
	 */
	protected abstract Image createTypeIcon();
}