package net.sourceforge.coffea.editors.views;

import net.sourceforge.coffea.editors.CoffeaEditorsPlugin;
import net.sourceforge.coffea.editors.figures.ClassFigure;
import net.sourceforge.coffea.editors.figures.InterfaceFigure;
import net.sourceforge.coffea.editors.figures.PackageFigure;
import net.sourceforge.coffea.uml2.model.IClassService;
import net.sourceforge.coffea.uml2.model.IElementService;
import net.sourceforge.coffea.uml2.model.IInterfaceService;
import net.sourceforge.coffea.uml2.model.IModelService;
import net.sourceforge.coffea.uml2.model.IPackageService;
import net.sourceforge.coffea.uml2.model.ITypeService;
import net.sourceforge.coffea.uml2.model.creation.IModelServiceBuilding;
import net.sourceforge.coffea.uml2.model.creation.ModelServiceBuilder;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** View of the current selection as UML */
public class UMLSelectionViewPart 
extends ViewPart implements ISelectionListener {

	/** Figure displayed on selection change */
	protected Figure displayedFigure;

	/** System displaying the figure */
	protected LightweightSystem figureSystem;

	/** Processor building services on selection change */
	protected IModelServiceBuilding serviceBuilder;

	/** Service for the selected element */
	protected IElementService selectedElementService;

	/** Service for a model of the selected project */
	protected IModelService projectModelService;

	@Override
	public void createPartControl(Composite parent) {
		Composite parentComposite = parent;
		IWorkbenchWindow workbenchWindow = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		String sourceViewId = null;
		if(workbenchWindow!=null) {
			IWorkbenchPage[] pages = workbenchWindow.getPages();
			if(pages!=null) {
				// For each of these pages...
				IWorkbenchPage page = null;
				//ISelection selection = null;
				for(int i=0 ; i<pages.length ; i++) {
					page = pages[i];
					IWorkbenchPartReference activePart = 
						page.getActivePartReference();
					if(activePart instanceof IViewReference) {
						IViewReference viewPart =(IViewReference)activePart;
						sourceViewId = viewPart.getId();
					}
				}
			}
		}
		IWorkbenchWindow win = getSourceWorkbenchWindow();
		serviceBuilder = new ModelServiceBuilder(sourceViewId, win);
		Composite containerComposite = new Composite(parentComposite, SWT.NONE);
		containerComposite.setLayout(new FillLayout());
		Canvas canvas = new Canvas(containerComposite, SWT.NULL);
		canvas.setLayout(new RowLayout(SWT.VERTICAL));
		figureSystem = new LightweightSystem(canvas);
		getSite().getPage().addSelectionListener(this);
	}
	
	protected IWorkbenchWindow getSourceWorkbenchWindow() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win;
	}

	@Override
	public void setFocus() {
		if(displayedFigure != null) {
			displayedFigure.requestFocus();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if(selection instanceof TreeSelection) {
			TreeSelection treeSelect = (TreeSelection)selection;
			Object firstElement = treeSelect.getFirstElement();
			IType typeEl = null;
			IPackageFragment packageEl = null;
			if(firstElement instanceof IType) {
				typeEl = (IType)firstElement;
			}
			else if(firstElement instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit)firstElement;
				try {
					typeEl = unit.getTypes()[0];
				} catch (JavaModelException e) {
					CoffeaEditorsPlugin.getDefault().logError(null, e);
				}
			}
			else if(firstElement instanceof IPackageFragment) {
				packageEl = (IPackageFragment)firstElement;
			}
			if(typeEl != null) {
				ITypeService<?, ?> typeService = 
					serviceBuilder.buildTypeService(
							typeEl.getCompilationUnit(), 
							null
					);
				if(typeService instanceof IClassService) {
					IClassService<?, ?> srv = (IClassService<?, ?>)typeService;
					selectedElementService = srv;
					displayedFigure = new ClassFigure(srv);
				}
				else if(typeService instanceof IInterfaceService) {
					IInterfaceService<?, ?> srv = 
						(IInterfaceService<?, ?>)typeService;
					selectedElementService = srv;
					displayedFigure = new InterfaceFigure(srv);
				}
				else {
					displayedFigure = null;
				}
				display(displayedFigure);
			}
			else if(packageEl != null) {
				IPackageService packageService = 
					serviceBuilder.buildPackageService(packageEl);
				if(packageService != null) {
					displayedFigure = 
						new PackageFigure<IPackageService>(packageService);
				}
				else {
					displayedFigure = null;
				}
				display(displayedFigure);
			}
			else {
				// figureSystem.setContents(null);
				display(new Figure());
			}
		}
	}
	
	public void display(Figure displayedFigure) {
		Figure fig = new Figure();
		fig.setLayoutManager(new FlowLayout(true));
		fig.add(displayedFigure);
		figureSystem.setContents(fig);
	}

}