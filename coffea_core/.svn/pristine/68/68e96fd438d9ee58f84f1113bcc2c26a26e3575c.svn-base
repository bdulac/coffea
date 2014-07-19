package net.sourceforge.coffea.uml2.model.impl;

import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IContainableElementService;
import net.sourceforge.coffea.uml2.model.IContainerService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.ITypesOwnerContainableService;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.uml2.uml.Association;

/** Service for an association */
public abstract class AssociationService extends
		MemberService<Association, FieldDeclaration, IField> {

	/** Tool for manipulating the association supplier */
	protected ITypeService<?, ?> supplier;

	public AssociationService(ITypesOwnerContainableService p, String nm) {
		super(p, nm);
	}
	
	/**
	 * Construction
	 * @param stxNode
	 * 	Value of {@link #syntaxTreeNode}, association supplier declaration
	 * @param p
	 * 	Value of {@link #container}, service for the association client
	 */
	protected AssociationService(
			FieldDeclaration stxNode, 
			IClassService<?, ?> p
	) {
		super(stxNode, p);
	}

	/**
	 * Construction
	 * @param jEl
	 * 	Value of {@link #javaElement}, association supplier declaration
	 * @param p
	 * 	Value of {@link #container}, service for the association client
	 */
	protected AssociationService(
			IField jEl, 
			IClassService<?, ?> p
	) {
		super(jEl, p);
	}

	public String getSimpleName() {
		String declaredName = null;
		if(syntaxTreeNode!=null) {
			if(
					(syntaxTreeNode.fragments()!=null)
					&&(syntaxTreeNode.fragments().size()>0)
					&&(syntaxTreeNode.fragments().get(0)!=null)
			) {
				// Adjusting the declared name which is, in the AST syntax 
				// point of view, followed by the '=' character
				declaredName = 
					syntaxTreeNode.fragments().get(0).toString();
				if(declaredName.lastIndexOf('=')!=-1) {
					declaredName = 
						declaredName.substring(
								0, 
								declaredName.indexOf('=')
						);
				}
				declaredName = declaredName + "[AssociationSupply]";
			}
		}
		else if(javaElement!=null) {
			declaredName = javaElement.getElementName();
			declaredName = declaredName + "[AssociationSupply]";
		}
		return declaredName;
	}
	
	@Override
	public IClassService<?, ?> getContainerService() {
		return (IClassService<?, ?>)container;
	}

	public ITypeService<?, ?> resolveSupplierService() {
		String typeName = null;
		boolean imported = false;
		// If we don't have a tool for manipulating the supplier,
		if(syntaxTreeNode!=null) {
			if(syntaxTreeNode.getType() instanceof SimpleType) {
				// Then we try to find it in the model
				SimpleType supplierType = 
					(SimpleType)syntaxTreeNode.getType();
				ITypeBinding binding = supplierType.resolveBinding();
				if(binding!=null) {
					typeName = binding.getQualifiedName();
				}
			}
		}
		else if(
				(javaElement!=null)
				&&(javaElement.getTypeRoot()!=null)
		) {
			try {
				IImportDeclaration[] imports = null;
				// Aiming to get the field type fully qualified name, 
				if(container instanceof IClassService<?, ?>) {
					// We try to get the containing class imports
					IClassService<?, ?> cl = (IClassService<?, ?>)container;
					imports = 
						cl.getJavaElement().getCompilationUnit().getImports();
				}
				typeName = javaElement.getTypeSignature();
				if(typeName!=null) {
					// If we don't have the type fully qualified name, 
					if(typeName.startsWith("Q")) {
						// We get the simple name
						typeName = typeName.substring(1, typeName.length()-1);
						IImportDeclaration imp = null;
						// And try to resolve the full name from the imports
						if(imports!=null) {
							for(int i=0 ; i<imports.length ; i++) {
								imp = imports[i];
								if(imp.getElementName().endsWith(typeName)) {
									typeName = imp.getElementName();
									imported = true;
									break;
								}
							}
							/*
							if(!resolved) {
								// If the full name was not resolved, 
								// then it is the java.lang package
								typeName = "java.lang." + typeName;
							}
							*/
						}
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		if(typeName!=null) {
			// If the type has been imported, 
			if(imported) {
				// Then we look for it in the model
				supplier = getModelService().resolveTypeService(typeName);
			}
			else {
				// Else we got to get the local package
				IPackageService packH = null;
				IContainerService contH = getContainerService();
				while(
						(contH != null)
						&&(!(contH instanceof IPackageService))
						&&(contH instanceof IContainableElementService<?, ?>)
				) {
					
					contH = 
						((IContainableElementService<?, ?>)contH)
						.getContainerService();
				}
				// From the package we can have the type
				if(contH instanceof IPackageService) {
					packH = (IPackageService)contH;
					String packName = packH.getFullName();
					String fullName = packName + '.' + typeName;
					supplier = packH.resolveTypeService(fullName);
				}
			}
		}
		return supplier;
	}

	/**
	 * Returns the client class handler
	 * @return Client class handler
	 */
	public IClassService<?, ?> getClient() {
		return (IClassService<?, ?>)this.container;
	}

}