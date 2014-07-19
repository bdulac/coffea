package net.sourceforge.coffea.editors.figures;

import java.util.List;

import net.sourceforge.coffea.uml2.model.IAttributeService;

/** Figure displaying a set of properties */
public class PropertiesFigure extends MemberSetFigure<IAttributeService> {
	
	/**
	 * Construction of a figure displaying a set of properties
	 * @param dsplMbrsSrvs
	 * List of services for all the displayed properties
	 */
	public PropertiesFigure(List<IAttributeService> dsplMbrsSrvs) {
		super(dsplMbrsSrvs);
		for(IAttributeService propertySrv : displayedMembersServices) {
			add(new PropertyFigure(propertySrv));
		}
	}

}