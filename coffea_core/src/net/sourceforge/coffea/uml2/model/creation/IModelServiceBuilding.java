package net.sourceforge.coffea.uml2.model.creation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;

import net.sourceforge.coffea.uml2.model.IModelService;

/** Ability to build services */
public interface IModelServiceBuilding 
extends IJavaElementServiceBuilding, IJavaFileParsing {

	/**
	 * Returns the latest built model service
	 * @return Latest built model service
	 */
	public IModelService getLatestModelServiceBuilt();

	/**
	 * TODO Should only appear on the service
	 * Saves the model in the underlying project
	 */
	public void save();
	
	/**
	 * TODO Should only appear on the service
	 * Saves the model to the underlying project
	 */
	public void save(IProgressMonitor monitor);

	public IWorkbenchWindow getSourceWorkbenchWindow();

	public String getSourceViewId();
	
	/**
	 * Returns the source view identifier
	 * @return Source view identifier in the 
	 * {@link #getSourceWorkbenchWindow() source workbench window}
	 */
	// public String getSourceViewId();

}