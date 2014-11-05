package net.sourceforge.coffea.uml2.model;

import org.eclipse.uml2.uml.Comment;

public interface INoteService extends IElementService {

	/** @return Comment handled by the service */
	public Comment getUMLElement();
	
}
