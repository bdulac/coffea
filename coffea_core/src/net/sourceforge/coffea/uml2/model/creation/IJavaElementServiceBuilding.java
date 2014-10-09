package net.sourceforge.coffea.uml2.model.creation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;

import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.ITypeService;

/** 
 * Ability to produce UML models from <em>Java</em> elements contained in 
 * the workspace
 */
public interface IJavaElementServiceBuilding {
	
	/**
	 * Processes a  path to build a {@link IModelService model service}
	 * @param modelPath
	 * 	The path too process
	 * @return Built {@link IModelService model service}
	 */
	public IModelService buildModelService(String modelPath);
	
	/**
	 * Processes a {@link IJavaElement <em>Java</em> element} to build a 
	 * {@link IModelService model service}
	 * @param el
	 * The {@link IJavaElement <em>Java</em> element} to process
	 * @param monitor
	 * Progression monitor
	 * @return Built {@link IModelService model service}
	 */
	public IModelService buildModelService(
			IJavaElement el, 
			IProgressMonitor monitor
	);
	
	/**
	 * Build a type service given a compilation unit and adds it to a 
	 * dedicated model service
	 * @param c
	 * Compilation unit for which a type service must be built
	 * @param mon
	 * Monitor measuring the operation progress
	 * @return Built type service
	 */
	public ITypeService<?, ?> buildTypeService(
			ICompilationUnit c, 
			IProgressMonitor mon
	);
	
	/**
	 * Processes a type service given a compilation unit and adds it to the 
	 * latest model service built
	 * @param c
	 * Compilation unit to process
	 * @param mon
	 * Monitor measuring the operation progress
	 * @return Processed type handler
	 */
	public ITypeService<?, ?> processTypeService(
			ICompilationUnit c, 
			IProgressMonitor mon
	);
	
	/**
	 * Builds a package service given a package fragment and ands it to a 
	 * dedicated model
	 * @param packageFragment
	 * Package fragment for which a package service must be built
	 * @return Built type service
	 */
	public IPackageService buildPackageService(
			IPackageFragment packageFragment
	);
}