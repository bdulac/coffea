package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IASTNodeService;
import net.sourceforge.coffea.uml2.model.IContainableElementService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.INoteService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;

public class JavadocService<A extends IContainableElementService<?, ?>> 
extends ElementService<Comment> implements INoteService {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -9190763359101571574L;

	/** AST node to which the Javadoc is attached */
	private IContainableElementService<?, ?> subject;
	
	public JavadocService(A p) {
		if(p == null)throw new NullPointerException();
		subject = p;
		
		// TODO Auto-generated constructor stub
	}
	
	public String getText() {
		String doc = null;
		IJavaElement jEl = subject.getJavaElement();
		if(jEl != null) {
			try {
				// FIXME Doesn't work: always returns null (beacuse source) !
				// See Javadoc, no workaround found
				doc = jEl.getAttachedJavadoc(new NullProgressMonitor());
			} catch (JavaModelException e) {
			}
		}
		return doc;
	}
	
	@Override
	protected void loadExistingUmlElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createUmlElement() {
		String text = getText();
		if(text != null) {
			Element subjectEl = subject.getUMLElement();
			umlModelElement = subjectEl.createOwnedComment();
			umlModelElement.setBody(text);
		}
	}

	public IModelService getModelService() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSimpleName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element findEditorUMLElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public void acceptModelChangeNotification(Notification nt) {
		// TODO Auto-generated method stub
		
	}

	public NotificationFilter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	public void resourceSetChanged(ResourceSetChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
