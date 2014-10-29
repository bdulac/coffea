package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IAssociationService;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.impl.CompositionService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;

/** Service for a composition */
public class CompositionService 
extends AssociationService 
implements IAssociationService<FieldDeclaration, IField> {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = 8377039626908942363L;

	/**
	 * Composition service construction from an AST node
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}, composition supplier declaration
	 * @param p
	 * Value of {@link #container}, composition client handler
	 */
	protected CompositionService(
			FieldDeclaration stxNode, 
			IClassService<?, ?> p
	) {
		super(stxNode, p);
	}

	/**
	 * Construction service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}, composition supplier declaration
	 * @param p
	 * Value of {@link #container}, composition client handler
	 */
	protected CompositionService(
			IField jEl, 
			IClassService<?, ?> p
	) {
		super(jEl, p);
	}
	
	@Override
	protected void loadExistingUmlElement() {
		ITypeService<?, ?> sp = resolveSupplierService();
		if(sp != null) {
			if(supplier != null) {
				EList<Association> associations = 
						getClient().getUMLElement().getAssociations();
				Type endType = supplier.getUMLElement();
				VisibilityKind visibility = getVisibility();
				for(Association association : associations) {
					if(
							association.getEndTypes().contains(endType)
							&&(association.getVisibility().equals(visibility))
					) {
						umlModelElement = association;
						break;
					}
				}
			}
		}
	}
	
	@Override
	protected void createUmlElement() {
		ITypeService<?, ?> sp = resolveSupplierService();
		if(sp != null) {
			Type endType = supplier.getUMLElement();
			umlModelElement =
				getClient().getUMLElement().createAssociation(
						false, 
						AggregationKind.COMPOSITE_LITERAL, 
						null, 
						1, 
						1, 
						endType, 
						false, 
						AggregationKind.NONE_LITERAL, 
						null, 
						0, 
						-1
				);
			umlModelElement.setVisibility(getVisibility());
			if(
					(syntaxTreeNode!=null)
					&&(syntaxTreeNode.getJavadoc()!=null)
			) {
				Comment docComment = 
					umlModelElement.createOwnedComment();
				docComment.setBody(
						syntaxTreeNode.getJavadoc().toString()
				);
			}
			else if(javaElement != null) {
				String doc = null;
				try {
					doc = 
						javaElement.getAttachedJavadoc(
							new NullProgressMonitor()
						);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				umlModelElement.setName(doc);
			}
		}
	}

	// @Override
	public Element findEditorUMLElement() {
		return umlModelElement;
	}

	// @Override
	public void acceptModelChangeNotification(Notification nt) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	public NotificationFilter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event)
			throws RollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}