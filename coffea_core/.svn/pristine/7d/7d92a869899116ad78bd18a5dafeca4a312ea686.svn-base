package net.sourceforge.coffea.uml2;

import java.lang.reflect.InvocationTargetException;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** Plug-in class */
public class CoffeaUML2Plugin extends Plugin {

	// The shared instance
	private static CoffeaUML2Plugin plugin;
	
	private static final String ID = "net.sourceforge.coffea.uml2";
	
	public static CoffeaUML2Plugin getInstance() {
		return plugin;
	}
	
	/**
	 * The constructor
	 */
	public CoffeaUML2Plugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the plug-in shared instance
	 * @return Shared instance
	 */
	public static CoffeaUML2Plugin getDefault() {
		return plugin;
	}
	
	public void logError(String error, Throwable throwable) {
		if (error == null && throwable != null) {
			error = throwable.getMessage();
		}
		getLog().log(
				new Status(IStatus.ERROR, ID, IStatus.OK, error, throwable)
		);
	}
	
	public void execute(IUML2RunnableWithProgress runnable) {
		try {
			runnable.run(new NullProgressMonitor());
			/*
			 * PlatformUI.getWorkbench().getProgressService().run(
						true, 
						true,
						runnable
				);
			 */
		} catch (InvocationTargetException e) {
			logError(e.getMessage(), e);
		} catch (InterruptedException e) {
			logError(e.getMessage(), e);
		}
	}
}
