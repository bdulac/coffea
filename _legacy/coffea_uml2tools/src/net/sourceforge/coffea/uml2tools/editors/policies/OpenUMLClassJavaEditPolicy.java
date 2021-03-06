package net.sourceforge.coffea.uml2tools.editors.policies;

import net.sourceforge.coffea.uml2.model.IClassService;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.AbstractCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.uml2.uml.NamedElement;

/** {@link UMLClassDiagramJavaEditor Class} edit policy */
public class OpenUMLClassJavaEditPolicy extends OpenEditPolicy {

	/** Handler for the class to open */
	protected IClassService<?, ?> classHandler;
	
	/** Opened editor part */
	protected IEditorPart editPart;

	/**
	 * {@link UMLClassDiagramJavaEditor Class} edit policy construction
	 * @param clH
	 * Handler for the class to open
	 */
	public OpenUMLClassJavaEditPolicy(IClassService<?, ?> clH) {
		classHandler = clH;
	}

	protected Command getOpenCommand(Request request) {

		return new ICommandProxy(
				new OpenClassCommand(classHandler.getFullName())
		);
	}

	protected class OpenClassCommand extends AbstractCommand {

		public OpenClassCommand(String label) {
			super(label);
		}

		@Override
		protected CommandResult doExecuteWithResult(
				IProgressMonitor progressMonitor, IAdaptable info)
				throws ExecutionException {
			Exception exReturn = null;
			if(classHandler!=null) {
				try {
					editPart = JavaUI.openInEditor(classHandler.getProcessedUnit());
					return CommandResult.newOKCommandResult();
				} catch (PartInitException e) {
					exReturn = e;
				} catch (JavaModelException e) {
					exReturn = e;
				}
			}
			else {
				exReturn = new NullPointerException();
			}
			return CommandResult.newErrorCommandResult(exReturn);
		}

		@Override
		protected CommandResult doRedoWithResult(
				IProgressMonitor progressMonitor, IAdaptable info)
				throws ExecutionException {
			return doExecuteWithResult(progressMonitor, info);
		}

		@Override
		protected CommandResult doUndoWithResult(
				IProgressMonitor progressMonitor, IAdaptable info)
				throws ExecutionException {
			return null;
		}
		

		
	}

	protected static String getDiagramName(EObject diagramDomainElement) {
		String result = null;
		if (diagramDomainElement instanceof NamedElement) {
			NamedElement named = (NamedElement) diagramDomainElement;
			result = named.getQualifiedName();
			if (result == null || result.length() == 0) {
				result = named.getName();
			}
		}
		return result;
	}

}