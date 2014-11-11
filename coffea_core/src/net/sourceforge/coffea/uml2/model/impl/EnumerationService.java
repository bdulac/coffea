package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IGroupService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;
import net.sourceforge.coffea.uml2.model.ITypesContainerService;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.UMLFactory;

/** Service for an interface */
public class EnumerationService 
extends ClassifierService<Enumeration, TypeDeclaration, IType>
implements IInterfaceService<TypeDeclaration, IType>{

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -1693022755248903916L;

	/**
	 * Interface service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	public EnumerationService(ITypesContainerService p, String nm) {
		super(p, nm);
	}

	/**
	 * Interface service construction from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 * @param u
	 * Value of {@link #processedUnit}
	 */
	public EnumerationService(
			IType jEl, 
			ITypesContainerService p, 
			ICompilationUnit u
	) {
		super(jEl, p, u);
	}
	
	@Override
	protected void createUmlElement() {
		IGroupService parent = getContainerService();
		String name = null;
		if((syntaxTreeNode != null)&&(syntaxTreeNode.getName() != null)) {
			name = syntaxTreeNode.getName().getFullyQualifiedName();
		}
		else if(javaElement != null) {
			name = javaElement.getElementName();
		}
		else if(defaultSimpleName != null) {
			name = getSimpleName();
		}
		if(name != null) {
			Element parentEl = parent.getUMLElement();
			if (parentEl instanceof org.eclipse.uml2.uml.Package) {
				org.eclipse.uml2.uml.Package packEl = 
						(org.eclipse.uml2.uml.Package) parentEl;
				umlModelElement = packEl.createOwnedEnumeration(name);
			}
			else if (parentEl instanceof Classifier) {
				Classifier cl = (Classifier) parentEl;
				umlModelElement = UMLFactory.eINSTANCE.createEnumeration();
				umlModelElement.setName(name);
				if (cl instanceof org.eclipse.uml2.uml.Class) {
					org.eclipse.uml2.uml.Class cla = 
							(org.eclipse.uml2.uml.Class) cl;
					cla.getNestedClassifiers().add(umlModelElement);
				} else if (cl instanceof Enumeration) {
					Enumeration en = (Enumeration) cl;
					en.getMembers().add(umlModelElement);
				}
			}
			umlModelElement.setVisibility(getVisibility());
		}
		if(syntaxTreeNode != null) {
			Javadoc doc = syntaxTreeNode.getJavadoc();
			if(doc != null) {
				Comment docComment = 
					umlModelElement.createOwnedComment();
				docComment.setBody(doc.toString());				
			}
		}
	}
}