package net.sourceforge.coffea.editors.figures;

import java.util.List;

import org.eclipse.draw2d.Label;

import net.sourceforge.coffea.editors.CoffeaEditorsPlugin;
import net.sourceforge.coffea.uml2.model.IClassService;

/** Figure displaying a set of classes */
public class ClassesFigure extends MemberSetFigure<IClassService<?, ?>> {
	
	/** Flag extending the display of classes */
	protected boolean extendedDisplay;

	/**
	 * Construction of a figure displaying a set of classes
	 * @param dsplMbrsSrvs
	 * List of services for all the displayed classes
	 */
	public ClassesFigure(List<IClassService<?, ?>> dsplMbrsSrvs) {
		super(dsplMbrsSrvs);
		extendedDisplay = 
			CoffeaEditorsPlugin.getDefault().isSelectionDetailed();
		for(IClassService<?, ?> classSrv : displayedMembersServices) {
			if(extendedDisplay) {
				add(new ClassFigure(classSrv));
			}
			else {
				Label classLabel = 
					new Label(
							classSrv.getSimpleName(), 
							ClassFigure.createClassIcon()
					);
				add(classLabel);
			}
		}
	}
}