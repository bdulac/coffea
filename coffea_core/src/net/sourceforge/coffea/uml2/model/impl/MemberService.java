package net.sourceforge.coffea.uml2.model.impl;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.coffea.uml2.CoffeaUML2Plugin;
import net.sourceforge.coffea.uml2.IUML2RunnableWithProgress;
import net.sourceforge.coffea.uml2.Resources;
import net.sourceforge.coffea.uml2.model.IASTNodeService;
import net.sourceforge.coffea.uml2.model.IContainerService;
import net.sourceforge.coffea.uml2.model.IMemberService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.ITypesOwnerContainableService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.sef.SelfEncapsulateFieldRefactoring;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.VisibilityKind;

/** 
 * Service for a member of a class
 * @param <E>
 * Type of the class member handled by the service as UML element
 * @param <S> 
 * Type of the class member handled by the service as AST node
 * @param <J>
 * Type of the class member handled by the service as Java element
 */
public abstract class MemberService
<E extends NamedElement, S extends BodyDeclaration, J extends IMember> 
extends ASTNodeService<E, S, J> 
implements IMemberService<S, J> {

	/** @see java.io.Serializable */
	private static final long serialVersionUID = -8807849102977391742L;

	/**
	 * Member service construction without any declaration
	 * @param p
	 * Value of {@link #container}
	 * @param nm
	 * Value of {@link #defaultSimpleName}
	 */
	protected MemberService(ITypesOwnerContainableService p, String nm) {
		super(p, nm);
	}

	/**
	 * Member service from a Java element
	 * @param jEl
	 * Value of {@link #javaElement}
	 * @param p
	 * Value of {@link #container}
	 */
	protected MemberService(
			J jEl,
			ITypesOwnerContainableService p
	) {
		super(jEl, p);
	}

	public IModelService getModelService() {
		if(this instanceof IModelService) {
			return (IModelService)this;
		}
		else {
			return getContainerService().getModelService();
		}
	}

	@Override
	public ITypesOwnerContainableService getContainerService() {
		return (ITypesOwnerContainableService)container;
	}

	public VisibilityKind getVisibility() {
		VisibilityKind visibility = VisibilityKind.PACKAGE_LITERAL;
		if(syntaxTreeNode!=null) {
			for (
					int i = 0; 
					i < syntaxTreeNode.modifiers().size(); 
					i++
			) {
				if (
						syntaxTreeNode.modifiers().get(i).toString()
						.equals(
								Resources.getCodeConstant(
										"constants.privateVisibility"
								)
						)
				) {
					return VisibilityKind.PRIVATE_LITERAL;
				}
				else if(
						syntaxTreeNode.modifiers().get(i).toString()
						.equals(
								Resources.getCodeConstant(
										"constants.protectedVisibility"
								)
						)
				) {
					return VisibilityKind.PROTECTED_LITERAL;
				}
				else if(
						syntaxTreeNode.modifiers().get(i).toString()
						.equals(
								Resources.getCodeConstant(
										"constants.publicVisibility"
								)
						)
				) {
					return VisibilityKind.PUBLIC_LITERAL;
				}
			}
		}
		else if(javaElement!=null) {
			int fl;
			try {
				fl = javaElement.getFlags();
				if(Flags.isPrivate(fl)) {
					return VisibilityKind.PRIVATE_LITERAL;
				}
				if(Flags.isProtected(fl)) {
					return VisibilityKind.PROTECTED_LITERAL;
				}
				if(Flags.isPublic(fl)) {
					return VisibilityKind.PUBLIC_LITERAL;
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}
		}
		return visibility;
	}

	public boolean isStatic() {
		if(syntaxTreeNode!=null) {
			for (int i = 0; i < syntaxTreeNode.modifiers().size(); i++) {
				if(
						syntaxTreeNode.modifiers().get(i).toString()
						.equals(
								Resources.getCodeConstant(
										"constants.staticModifier"
								)
						)) {
					return true;
				}
			}
		}
		else if(javaElement!=null) {
			try {
				int fl = javaElement.getFlags();
				if(Flags.isStatic(fl)) {
					return true;
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}

		}
		return false;
	}

	public boolean isAbstract() {
		if((syntaxTreeNode!=null)&&(syntaxTreeNode.modifiers()!=null)) {
			for (int i=0 ; i<syntaxTreeNode.modifiers().size() ; i++) {
				if(
						syntaxTreeNode.modifiers().get(i).toString()
						.equals(
								Resources.getCodeConstant(
										"constants.abstractModifier"
								)
						)
				)
					return true;
			}
		}
		else if(javaElement!=null) {
			try {
				int fl = javaElement.getFlags();
				if(Flags.isAbstract(fl)) {
					return true;
				}
			} catch (JavaModelException e) {
				CoffeaUML2Plugin.getInstance().logError(e.getMessage(), e);
			}

		}
		return false;
	}
	
	/*
	@Override
	public void acceptModelChangeNotification(Notification nt) {
		super.acceptModelChangeNotification(nt);
		if((nt!=null)&&(nt.getNotifier()!=null)) {
			Object feature = nt.getFeature();
			if (feature instanceof EAttribute) {
				EAttribute attr = (EAttribute) feature;
				String attrName = attr.getName();
				Object newValue = nt.getNewValue();
				if(attrName != null) {
					if(attrName.equals(VISIBILITY_ATTRIBUTE)) {
						if (newValue instanceof VisibilityKind) {
							String newVisibility = 
								((VisibilityKind)newValue).getLiteral();
							// We avoid changing visibility if the new value 
							// is impossible
							boolean avoid = 
								!(
										newVisibility.equals(
												VisibilityKind
												.PRIVATE_LITERAL
												.getLiteral()
										)
										||
										newVisibility.equals(
												VisibilityKind
												.PACKAGE_LITERAL
												.getLiteral()
										)
										||
										newVisibility.equals(
												VisibilityKind
												.PROTECTED_LITERAL
												.getLiteral()
										)
										||
										newVisibility.equals(
												VisibilityKind
												.PUBLIC_LITERAL
												.getLiteral()
										)
								);
							if(!avoid) {
								changeVisibility(newVisibility);
							}
						}
					}
				}
			}
		}
	}
	*/

	public void changeVisibility(String visibilityLiteral) {
		ChangingVisibilityRunnable runnable = 
			new ChangingVisibilityRunnable(visibilityLiteral);
		CoffeaUML2Plugin.getInstance().execute(runnable);
	}

	/** Visibility change runnable */
	public class ChangingVisibilityRunnable 
	implements IUML2RunnableWithProgress {

		/** 
		 * New visibility kind {@link VisibilityKind#getLiteral() literal 
		 * value}
		 */
		protected String newVisiblityKindLiteral;

		/**
		 * Visibility change runnable construction
		 * @param nv
		 * Value of {@link #newVisiblityKindLiteral}
		 */
		public ChangingVisibilityRunnable(String nv) {
			newVisiblityKindLiteral = nv;
		}

		/*
		private void addAdjustment(
				IMember whoToAdjust, 
				ModifierKeyword neededVisibility, 
				Map<IJavaElement, IVisibilityAdjustment> adjustments
		) throws JavaModelException {
			ModifierKeyword currentVisibility = 
				ModifierKeyword.fromFlagValue(
						JdtFlags.getVisibilityCode(whoToAdjust)
				);
			if (
					MemberVisibilityAdjustor
					.hasLowerVisibility(
							currentVisibility, 
							neededVisibility
					)
					&& 
					MemberVisibilityAdjustor
					.needsVisibilityAdjustments(
							whoToAdjust, 
							neededVisibility, 
							adjustments
					)
			) {
				adjustments.put(
						whoToAdjust, 
						new MemberVisibilityAdjustor
						.IncomingMemberVisibilityAdjustment(
								whoToAdjust, 
								neededVisibility,
								RefactoringStatus
								.createWarningStatus(
										Messages.format(
												MemberVisibilityAdjustor
												.getMessage(whoToAdjust), 
												new String[] {
													MemberVisibilityAdjustor
													.getLabel(whoToAdjust), 
													MemberVisibilityAdjustor
													.getLabel(neededVisibility)
												}
										), 
										JavaStatusContext.create(whoToAdjust)
								)
						)
				);
			}
		}
		*/

		public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
			if((javaElement != null) && (newVisiblityKindLiteral != null)) {
				IContainerService contH = getContainerService();
				if(contH instanceof IASTNodeService<?, ?>) {
					if(javaElement instanceof IField) {
						IField field = (IField)javaElement;
						try {
							SelfEncapsulateFieldRefactoring r = 
								new SelfEncapsulateFieldRefactoring(field);
							int visiblityInt = VisibilityKind.PRIVATE;
							if(
									newVisiblityKindLiteral.equals(
											VisibilityKind
											.PRIVATE_LITERAL
											.getLiteral()
									)
							) {
								visiblityInt = Flags.AccPrivate;
							}
							else if(
									newVisiblityKindLiteral.equals(
											VisibilityKind
											.PROTECTED_LITERAL
											.getLiteral()
									)
							) {
								visiblityInt = Flags.AccProtected;
							}
							else if(
									newVisiblityKindLiteral.equals(
											VisibilityKind
											.PUBLIC_LITERAL
											.getLiteral()
									)
							) {
								visiblityInt = Flags.AccPublic;
							}
							r.setVisibility(visiblityInt);
							r.setGetterName(
									Resources.getCodeConstant(
											"constants.getterPrefix"
									) 
									+ field.getElementName()
							);
							r.setSetterName(
									Resources.getCodeConstant(
											"constants.setterPrefix"
									)
									+ field.getElementName()
							);
							r.setGenerateJavadoc(true);
							PerformRefactoringOperation op = 
								new PerformRefactoringOperation(
										r, 
										CheckConditionsOperation
										.INITIAL_CONDITONS
									);
							op.run(monitor);
							/*
							 * 	PerformRefactoringOperation op = 
							 * 		new PerformRefactoringOperation(
							 * 			r, 
							 * 			CheckConditionsOperation
							 * 			.INITIAL_CONDITONS
							 * 	);
							 * 	op.run(monitor);
							 */
							
						} catch (JavaModelException e) {
							e.printStackTrace();
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
					/*
					 * IASTNodeHandling<?, ?> parentNode = 
						(IASTNodeHandling<?, ?>)contH;
					IJavaElement parentEl = parentNode.getJavaElement();
					MemberVisibilityAdjustor adjustor = 
						new MemberVisibilityAdjustor(parentEl, javaElement);
					Map<IJavaElement, IVisibilityAdjustment> adjustments = 
						new HashMap<IJavaElement, IVisibilityAdjustment>();
					ModifierKeyword keyword = 
						Modifier.ModifierKeyword.PUBLIC_KEYWORD;
					try  {
						if(
								newVisiblityKindLiteral.equals(
										VisibilityKind
										.PRIVATE_LITERAL
										.getLiteral()
								)
						) {
							keyword = Modifier.ModifierKeyword.PRIVATE_KEYWORD;
						}
						else if(
								newVisiblityKindLiteral.equals(
										VisibilityKind
										.PROTECTED_LITERAL
										.getLiteral()
								)
						) {
							keyword = 
								Modifier.ModifierKeyword.PROTECTED_KEYWORD;
						}
						else if(
								newVisiblityKindLiteral.equals(
										VisibilityKind
										.PUBLIC_LITERAL
										.getLiteral()
								)
						) {
							keyword = Modifier.ModifierKeyword.PUBLIC_KEYWORD;
						}
						addAdjustment(
								javaElement, 
								keyword, 
								adjustments
						);
						adjustor.setAdjustments(adjustments);
						adjustor.adjustVisibility(monitor);
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
					 */
				}
				// org.eclipse.jdt.internal.corext.refactoring.structure.

			}
		}

	}
}