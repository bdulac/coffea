package net.sourceforge.coffea.uml2.model;

import java.util.List;

/** Service for a group of packages */
public interface IPackagesGroupService 
extends IGroupService, ITypesContainerService {

	/**
	 * Returns the list of services handling the packages belonging to the
	 * handled element
	 * @return List of handlers handling the packages belonging to the
	 *         handled element
	 */
	public List<IPackageService> getPackagesServices();

	/**
	 * Resolves a service for a package contained in the handled group given 
	 * its name
	 * @param n
	 * Package full name
	 * @return Service corresponding to the name
	 * @see IElementService#getSimpleName()
	 */
	public IPackageService resolvePackageService(String n);
	
	/**
	 * Adds a service to the list of services handling the packages belonging 
	 * to the handled group
	 * @param pckSrv
	 * New service to add to the list
	 */
	public void addPackageService(IPackageService pckSrv);
	
	/**
	 * Retrieves the packages grouped inside the handled element (recursively)
	 * aiming to fetch the package hierarchy which has disappeared between the
	 * source code directories and the code AST parsing (analysis step). <b>It
	 * is important to note that each child has to
	 * {@link IPackageService#retrieveContainerFromHierarchy() recognize its
	 * parents first}.</b>
	 * @return List of services for packages contained in the handled 
	 * package group
	 */
	public List<IPackageService> fetchSubPackagesFromHierarchy();
	
}