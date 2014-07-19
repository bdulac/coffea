package net.sourceforge.coffea.uml2.model;

import java.util.List;
import org.eclipse.uml2.uml.Operation;

/** Service for an operations owner */
public interface IOperationsOwnerService extends IGroupService {

	/**
	 * Returns the list of services for the operations belonging to the
	 * handled operations owner
	 * @return List of services handling the operations belonging to the
	 * handled owner
	 */
	public List<IMethodService> getOperationsServices();

	/**
	 * Returns an service owned by the handled operation owner given the 
	 * operation name
	 * @param n
	 * Method name
	 * @return Service for the method corresponding to the name
	 * @see IElementService#getSimpleName()
	 */
	public IMethodService getOperationService(String n);
	
	/**
	 * Adds service to the list of services handling the operations 
	 * belonging to the handled owner
	 * @param methodService
	 * New service to add to the list
	 */
	public void addOperationService(IMethodService methodService);
	
	/**
	 * Creates an operation in the code owned by the handled element
	 * @param o
	 * Operation to create
	 * @return Service for the created operation
	 */
	public IMethodService createOperation(Operation o);

	/**
	 * Deletes from the code and operation owned by the handled element
	 * @param o
	 * Operation to remove
	 */
	public void deleteOperation(Operation o);
}