package net.sourceforge.coffea.uml2.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Classifier;

/** 
 * Service for an interface
 * @param <S>
 * Type of the interface handled by the service as AST node
 * @param <J>
 * Type of the interface handled by the service as Java element
 */
public interface IInterfaceService
<S extends TypeDeclaration, J extends IType>
extends IClassifierService<S, J> {
	
	/**
	 * Returns the interface handled by the service
	 * @return Interface handled by the service
	 */
	public Classifier getUMLElement();
	
	
	public ITypesContainerService getContainerService();
	
	/**
	 * Renames the interface in the code
	 * @param nm
	 * New interface simple name
	 */
	public void rename(String nm);
	
	/**
	 * Returns the compilation unit the interface belongs to
	 * @return Compilation unit the interface belongs to
	 */
	public ICompilationUnit getCompilationUnit();

	/**
	 * Returns the compilation unit the interface belongs to
	 * @return Compilation unit the interface belongs to
	 */
	public CompilationUnit getParsedUnit();
}