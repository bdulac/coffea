package net.sourceforge.coffea.editors.figures;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

import net.sourceforge.coffea.uml2.model.IMemberService;

/**
 * Figure displaying a set of classifier members
 * @param <M>
 * Type of the displayed classifier members service
 */
public class MemberSetFigure<M extends IMemberService<?, ?>> extends Figure {

	/** List of the displayed classifier members services */
	protected List<M> displayedMembersServices;
	
	/**
	 * Construction of a figure displaying a set of classifier members
	 * @param dsplMbrsSrvs
	 * List of services for all the displayed member
	 */
	public MemberSetFigure(List<M> dsplMbrsSrvs) {
		// super();
		if(dsplMbrsSrvs == null) {
			throw new IllegalArgumentException(
					"Missing members services"
			);
		}
		displayedMembersServices = dsplMbrsSrvs;
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(4);
		setLayoutManager(layout);
		setBorder(new CompartmentFigureBorder());
	}
	
	/**
	 * List the services for all the displayed members
	 * @return List of services for all the displayed member
	 */
	public List<M> getDisplayedMembersServices() {
		return displayedMembersServices;
	}
}