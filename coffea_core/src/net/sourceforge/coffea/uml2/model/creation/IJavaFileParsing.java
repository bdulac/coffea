package net.sourceforge.coffea.uml2.model.creation;

import java.io.File;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.ITypeService;

/** Ability to build a model by parsing <em>Java</em> source files */
public interface IJavaFileParsing {

	/**
	 * Parses a  part of a file system containing <em>Java</em> sources aiming 
	 * to build a {@link IModelService model handler}
	 * @param f
	 * 	Part of file system containing <em>Java</em> sources to parse
	 * @return Built {@link IModelService model handler}
	 */
	public IModelService parseFile(File f);
	
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