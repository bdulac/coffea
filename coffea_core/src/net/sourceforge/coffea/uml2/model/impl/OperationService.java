package net.sourceforge.coffea.uml2.model.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.coffea.uml2.model.IClassifierService;
import net.sourceforge.coffea.uml2.model.IMethodService;
import net.sourceforge.coffea.uml2.model.IOperationsOwnerService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.impl.MemberService;
import net.sourceforge.coffea.uml2.model.impl.OperationService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;


/** Service for a method */
public class OperationService 
extends MemberService<Operation, MethodDeclaration, IMethod> 
implements IMethodService {

	private static final long serialVersionUID = 564034904733931366L;

	/** 
	 * Service for the method return type
	 * @see #resolveReturnTypeService()
	 */
	protected ITypeService<?, ?> returnTypeHandler;

	/** 
	 * Method parameters names 
	 * @see #resolveParametersNames()
	 */
	protected List<String> parametersNames;

	/**
	 * List of services for the method parameters
	 * @see #resolveParametersTypesServices()
	 */
	protected List<ITypeService<?, ?>> parametersTypesHandlers;

	/**
	 * Operation service construction
	 * @param p
	 * Value of {@link #container}, <code>null</code> if 
	 * {@link #syntaxTreeNode} is a root
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 */
	protected OperationService(
			MethodDeclaration stxNode, 
			IClassifierService<?, ?> p
	) {
		super(stxNode, p);
		completeOperationConstruction(null, p);
	}

	/**
	 * Operation service construction given an existing <em>UML</em> element
	 * @param stxNode
	 * Value of {@link #syntaxTreeNode}
	 * @param own
	 * Value of {@link #container}
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	protected OperationService(
			MethodDeclaration stxNode, 
			IClassifierService<?, ?> own, 
			Operation ume
	) {
		super(stxNode, own, ume);
		completeOperationConstruction(null, own);
	}

	/**
	 * Operation service construction
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}, <code>null</code> if {@link #javaElement} 
	 * is a root
	 */
	protected OperationService(IMethod jEl, IClassifierService<?, ?> p) {
		super(jEl, p);
		completeOperationConstruction(null, p);
	}

	/**
	 * Operation service construction given an existing <em>UML</em> element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param own
	 * Value of {@link #container}
	 * @param ume
	 * Value of {@link #umlModelElement}
	 */
	protected OperationService(
			IMethod jEl, 
			IClassifierService<?, ?> own, 
			Operation ume
	) {
		super(jEl, own, ume);
		completeOperationConstruction(null, own);
	}
	
	protected void completeOperationConstruction(
			ASTRewrite r, 
			IOperationsOwnerService p
	) {
	}

	@Override
	public ITypeService<?, ?> getContainerService() {
		return (ITypeService<?, ?>)container;
	}

	public ITypeService<?, ?> resolveReturnTypeService() {
		ITypeService<?, ?> rType = null;
		String typeFullName = null;
		if(syntaxTreeNode != null) {
			//If we don't have a tool for manipulating the supplier,
			if(syntaxTreeNode.getReturnType2() instanceof SimpleType) {
				//Then we try to find it in the model
				SimpleType supplierType = 
					(SimpleType)syntaxTreeNode.getReturnType2();
				ITypeBinding binding = supplierType.resolveBinding();
				if(binding != null) {
					typeFullName = binding.getQualifiedName();
				}
			}
		}
		// If this operation has a return type,
		else if(javaElement != null) {
			try {
				if(javaElement.getReturnType() != null) {
					// The we note its name
					String simpleName = javaElement.getReturnType();
					simpleName = Signature.getSignatureSimpleName(simpleName);
					typeFullName = rebuildFullNameFromSimple(simpleName);
				}
			} catch(JavaModelException e) {
				e.printStackTrace();
			}
		}
		rType = getModelService().resolveTypeService(typeFullName);
		return rType;
	}

	public ITypeService<?, ?> getReturnTypeHandler() {
		if(returnTypeHandler == null) {
			returnTypeHandler = resolveReturnTypeService();
		}
		return returnTypeHandler;
	}

	public List<String> resolveParametersNames() {
		List<String> parametersNms = null;
		if(javaElement != null) {
			parametersNms = new ArrayList<String>();
			try {
				String[] names = javaElement.getParameterNames();
				for(int i = 0 ; i < names.length ; i++) {
					parametersNms.add(names[i]);
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		parametersNames = parametersNms;
		return parametersNms;
	}

	public List<String> getParametersNames() {
		if(parametersNames==null) {
			parametersNames = resolveParametersNames();
		}
		return parametersNames;
	}

	public List<ITypeService<?, ?>> resolveParametersTypesServices() {
		List<ITypeService<?, ?>> parametersTpsHs = null;
		if(javaElement != null) {
			parametersTpsHs = new ArrayList<ITypeService<?, ?>>();
			String[] typesSimpleNames = javaElement.getParameterTypes();
			/*
			String returnTypeName = null;
			try {
				returnTypeName = javaElement.getReturnType();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			String methodTest = null;
			String paramTpName = null;
			if(typesSimpleNames!=null) {
				IJavaProject prj = javaElement.getJavaProject();
				if(prj!=null) {
					// char[][] tpsSimplesNames = new char[typesSimpleNames.length][];
					IType tp = null;
					IClasspathEntry cpE = null;
					for(int i=0 ; i<typesSimpleNames.length ; i++) {
						try {
							tp = prj.findType(typesSimpleNames[i]);
							if(tp!=null) {
								tp = tp;
							}
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
						// methodTest = 
							// Signature.getSignatureQualifier(
								// typesSimpleNames[i]
						// );
					}
				}
			}
			*/
			for(int i = 0 ; i < typesSimpleNames.length ; i++) {
				String simpleName = typesSimpleNames[i];
				// TODO org.eclipse.jdt.core.Signature
				simpleName = Signature.getSignatureSimpleName(simpleName);
				String fullName = rebuildFullNameFromSimple(simpleName);
				ITypeService<?, ?> paramType = 
					getModelService().resolveTypeService(fullName);
				parametersTpsHs.add(paramType);
			}
		}
		parametersTypesHandlers = parametersTpsHs;
		return parametersTpsHs;
	}

	public List<ITypeService<?, ?>> getParametersTypesServices() {
		if(parametersTypesHandlers == null) {
			parametersTypesHandlers = resolveParametersTypesServices();
		}
		return parametersTypesHandlers;
	}

	@Override
	protected void createUmlElement() {
		if(
				(getContainerService() != null)
				&&(getContainerService() instanceof IClassifierService)
		) {
			IClassifierService<?, ?> clParent = 
					(IClassifierService<?, ?>)getContainerService();
			ITypeService<?, ?> returnTypeSrv = getReturnTypeHandler();
			Type rTypeElement = null;
			if(returnTypeSrv != null) {
				rTypeElement = returnTypeSrv.getUMLElement();
			}
			EList<String> paramsNames = new BasicEList<String>();
			EList<Type> paramsTypes = new BasicEList<Type>();
			paramsNames.addAll(getParametersNames());
			List<ITypeService<?, ?>> tSrvs = getParametersTypesServices();
			if(tSrvs != null) {
				ITypeService<?, ?> tpH = null;
				for(int i=0 ; i<tSrvs.size() ; i++) {
					tpH = tSrvs.get(i);
					if(tpH!=null) {
						Type t = tpH.getUMLElement();
						if(t!=null) {
							paramsTypes.add(t);
						}
					}
				}
			}
			String justName = null;
			if(javaElement != null) {
				justName = javaElement.getElementName();
				try {
					IJavaElement[] children = javaElement.getChildren();
					IJavaElement child = null;
					for(int i = 0 ; i < children.length ; i++) {
						child = children[i];
						if(child!=null) {
							switch (child.getElementType()) {
							case IJavaElement.METHOD:									
								break;
							default:
								break;
							}
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
			else if(syntaxTreeNode != null) {
				justName = syntaxTreeNode.getName().toString();
			}
			else {
				justName = getSimpleName();
			}
			Element parentElm = clParent.getUMLElement();
			if(parentElm instanceof DataType) {
				DataType dt = (DataType)parentElm;
				dt.createOwnedOperation(
						justName, 
						paramsNames,  
						paramsTypes, 
						rTypeElement
				);
			}
			else if(parentElm instanceof Interface) {
				Interface inter = (Interface)parentElm;
				umlModelElement = 
					inter.createOwnedOperation(
						justName, 
						paramsNames,  
						paramsTypes, 
						rTypeElement
					);
			}
			else if(parentElm instanceof Class) {
				Class cl = (Class)parentElm;
				umlModelElement = 
					cl.createOwnedOperation(
						justName, 
						paramsNames,  
						paramsTypes, 
						rTypeElement
					);
			}
			umlModelElement.setVisibility(getVisibility());
		}
	}
	
	@Override
	protected void loadExistingUmlElement() {
		// String name = getSimpleName();
		String name = javaElement.getElementName();
		Element parentElement = getContainerService().getUMLElement();
		/*
		ITypeService<?, ?> returnTypeSrv = resolveReturnTypeService();
		Type umlReturnType = null;
		if(returnTypeSrv != null){
			umlReturnType = returnTypeSrv.getUMLElement();
		}
		*/
		if(parentElement instanceof Class) {
			Class umlClass = (Class)parentElement;
			EList<String> paramsNames = new BasicEList<String>();
			paramsNames.addAll(getParametersNames());
			EList<Type> paramsTypes = new BasicEList<Type>();
			List<ITypeService<?, ?>> tSrvs = getParametersTypesServices();
			for(ITypeService<?, ?> tSrv : tSrvs) {
				if(tSrv != null)paramsTypes.add(tSrv.getUMLElement());
				else {
					// TODO Quid of primitive types ?
					/*
					CoffeaUML2Plugin.getInstance().logError(
							"Null type service for parameter of method "
							+ javaElement, 
							new NullPointerException()
					);
					*/
				}
			}
			ITypeService<?, ?> rTypeSrv = getReturnTypeHandler();
			if(rTypeSrv != null) {
				Type rType = rTypeSrv.getUMLElement();
				if(rType != null)paramsTypes.add(rType);
			}
			umlModelElement = 
					umlClass.getOperation(name, paramsNames, paramsTypes);
			if(umlModelElement != null) {
				VisibilityKind vis = getVisibility();
				if(!(umlModelElement.getVisibility() == vis)) {
					umlModelElement.setVisibility(vis);
				}
			}
		}
	}

	// @Override
	public String getSimpleName() {
		String name = null;
		// {@link CodeProcessor#readJavaFile(java.io.File, java.io.File)}
		if((syntaxTreeNode!=null)&&(syntaxTreeNode.getName()!=null)) {
			name =  syntaxTreeNode.getName().toString();
		}
		else if(javaElement!=null) {
			name = javaElement.getElementName();
		}
		List<ITypeService<?, ?>> tpsH = getParametersTypesServices();
		if(tpsH!=null) {
			ITypeService<?, ?> tpH = null;
			name += '(';
			for(int i=0 ; i<tpsH.size() ; i++) {
				tpH = tpsH.get(i);
				if(tpH!=null) {
					name+= tpH.getFullName();
				}
			}
			name += ')';
		}
		return name;
	}
	
	@Override
	public String toString() {
		String toString = null;
		if((syntaxTreeNode!=null)&&(syntaxTreeNode.getName()!=null)) {
			toString =  syntaxTreeNode.getName().getFullyQualifiedName();
		}
		else {
			toString = super.getFullName();
		}
		return toString;
	}
	
	@Override
	public void rename(String nm) {
		try {
			javaElement.rename(nm, false, new NullProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rebuild a full name from a local simple name
	 * @param simpleName
	 * Simple name from which the full name must be rebuilt
	 * @return Full name
	 */
	private String rebuildFullNameFromSimple(String simpleName) {
		String typeName = null;
		if(simpleName!=null) {
			if(simpleName.indexOf('<')>=0) {
				simpleName = 
					simpleName.substring(
							0, 
							simpleName.indexOf('<')
					);
			}
			ITypeService<?, ?> cont = getContainerService();
			String[][] namesParts;
			try {
				namesParts = cont.getJavaElement().resolveType(simpleName);
				String name = cont.nameReconstruction(namesParts);
				if(name != null) {
					typeName = name;
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return typeName;
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