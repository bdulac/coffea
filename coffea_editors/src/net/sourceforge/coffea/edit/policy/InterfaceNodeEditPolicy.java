package net.sourceforge.coffea.edit.policy;

import net.sourceforge.coffea.edit.InterfaceEditPart;
import net.sourceforge.coffea.uml2.model.IInterfaceService;

public class InterfaceNodeEditPolicy<M extends InterfaceEditPart<? extends IInterfaceService<?,?>>> 
extends ClassifierNodeEditPolicy<M> {

	public InterfaceNodeEditPolicy(M h) {
		super(h);
	}
}