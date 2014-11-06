package net.sourceforge.coffea.uml2.model;

import java.util.List;
import org.eclipse.uml2.uml.Operation;

/** Service for an operations owner */
public interface IOperationsOwnerService extends IGroupService {

	/**
	 * @return List of services handling the operations belonging to the 
	 * owner
	 */
	public List<IMethodService> getOperationsServices();

	/**
	 * Returns the service for an operation given the operation simple name
	 * @param n
	 * Method simple name
	 * @return Service for the method corresponding to the name
	 * @see IElementService#getSimpleName()
	 */
	public IMethodService getOperationService(String n);
	
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