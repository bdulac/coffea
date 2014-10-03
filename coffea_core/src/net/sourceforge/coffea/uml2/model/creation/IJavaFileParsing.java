package net.sourceforge.coffea.uml2.model.creation;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import net.sourceforge.coffea.uml2.model.ITypeService;

/** Ability to build a model by parsing <em>Java</em> source files */
public interface IJavaFileParsing {
	
	/**
	 * Process a type from parsed <em>Java</em> code
	 * @param t
	 * 	Type declaration
	 * @param c
	 * 	Compilation unit the type belongs to
	 * @return Processed type handler
	 */
	public ITypeService<?, ?> processParsedType(
			TypeDeclaration t, 
			CompilationUnit c
	);
	
}