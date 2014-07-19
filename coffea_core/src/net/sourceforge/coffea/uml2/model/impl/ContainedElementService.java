package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IClassifierService;
import net.sourceforge.coffea.uml2.model.IContainableElementService;
import net.sourceforge.coffea.uml2.model.IContainerService;
import net.sourceforge.coffea.uml2.model.IModelService;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.uml2.uml.NamedElement;

/**
 * Service for an element contained in another element
 * @param <E>
 * Type of the element handled by the service as UML element
 * @param <S> 
 * Type of the element handled by the service as AST node
 * @param <J>
 * Type of the element handled by the service as Java element
 */
public abstract class ContainedElementService
<E extends NamedElement, S extends ASTNode, J extends IJavaElement>
extends ElementService<E> 
implements IContainableElementService<S, J> {

	private static final long serialVersionUID = 4078749026823503144L;
	
	/** Element container handler */
	protected IContainerService container;

	/**
	 * Construction of a contained element service from the service of the 
	 * container element
	 * @param own
	 * Value of {@link #container}
	 */
	public ContainedElementService(IContainerService own) {
		super();
		container = own;
	}

	/**
	 * Construction of a contained element service from the service of the 
	 * container element and an UML element
	 * @param ume
	 * Value of {@link #umlModelElement}
	 * @param own
	 * Value of {@link #container}
	 */
	public ContainedElementService(IContainerService own, E ume) {
		super(ume);
		container = own;
	}

	public IContainerService getContainerService() {
		return container;
	}

	public String getFullName() {
		String name = new String();
		IContainerService ct = getContainerService();
		if(!(ct instanceof IModelService)) {
			String containerFullName = ct.getFullName();
			if(
					(
							!containerFullName.equals(
									IModelService.primitiveTypesPackageName
							)
					)
					&&(
							!containerFullName.equals(
									IModelService.classpathTypesPackageName
							)
					)
					&&(containerFullName.length()>0)
			) {
				if(!(ct instanceof IClassifierService<?, ?>)) {
					IModelService m = getModelService();
					if(
							!(
									(m!=null)
									&&(ct instanceof IModelService)
									&&(
											!containerFullName.equals(
													IModelService
													.defaultPackageFileName
											)
									)
							)
					) {
						name += containerFullName + '.';
					}
				}
				else {
					name += containerFullName + '#';
				}
			}
		}
		String simpleName = getSimpleName();
		/* 
		 * Removing  anything before any point in the simple name for 
		 * the hierarchy transition case (if we are not in the primitive types 
		 * or the class path)
		 */
		int ind = -1;
		IContainerService contH = getContainerService();
		if(
				((ind = simpleName.indexOf('.'))!=-1)
				&&(ind<simpleName.length())
				&&(contH!=null)
				&&(contH.getFullName()!=null)
				&&(contH.getFullName().length()>0)
		) {
			simpleName = simpleName.substring(ind +1);
		}
		name += simpleName;
		return name;
	}

	/*
	public NamedElement findEditorUMLElement() {
		NamedElement r = null;
		IContainerHandling cont = getContainerHandler();
		if(cont!=null) {
			Element parent = cont.findEditorUMLElement();
			if((parent!=null)&&(parent instanceof Namespace)) {
				Namespace nm = (Namespace)parent;
				EList<NamedElement> members = nm.getMembers();
				NamedElement member = null;
				String elementSimpleName = getSimpleName();
				for(int i=0 ; i<members.size() ; i++) {
					member = members.get(i);
					if((member!=null)&&(member.getName()!=null)) {
						if(member.getName().equals(elementSimpleName)) {
							r = member;
							break;
						}
					}
				}
			}
		}
		return r;
	}
	*/
}