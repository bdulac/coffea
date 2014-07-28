package net.sourceforge.coffea.editors.figures;

import java.util.List;

import net.sourceforge.coffea.uml2.model.IMethodService;

/** Figure displaying a set of operations */
public class OperationsFigure extends MemberSetFigure<IMethodService> {
	
	/**
	 * Construction of a figure displaying a set of operations
	 * @param dsplMbrsSrvs
	 * List of services for all the displayed operations
	 */
	public OperationsFigure(List<IMethodService> dsplMbrsSrvs) {
		super(dsplMbrsSrvs);
		for(IMethodService operationSrv : displayedMembersServices) {
			add(new OperationFigure(operationSrv));
		}
	}

}
