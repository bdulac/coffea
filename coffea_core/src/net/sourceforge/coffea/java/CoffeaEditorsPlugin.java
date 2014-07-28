package net.sourceforge.coffea.java;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plug-in class */
public class CoffeaEditorsPlugin extends AbstractUIPlugin {

	// The shared instance
	private static CoffeaEditorsPlugin plugin;

	// The plug-in ID
	public static final String ID = "net.sourceforge.coffea.editors";

	/**
	 * Returns the shared instance
	 * @return Shared instance
	 */
	public static CoffeaEditorsPlugin getDefault() {
		return plugin;
	}

	/** Plug-in construction */
	public CoffeaEditorsPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the flag indicating if the displayed selection will be detailed
	 * @return {@true} if the displayed selection will be detailed
	 */
	public boolean isSelectionDetailed() {
		return false;
	}

	/**
	 * Logs an error given a message and a {@link Throwable} detailing the 
	 * error
	 * @param msg
	 * Message explaining the malfunction - if null, the Throwable
	 * message value will be used
	 * @param throwable
	 * Throwable detailing the error
	 * 
	 */
	public void logError(String msg, Throwable throwable) {
		if (msg == null && throwable != null) {
			msg = throwable.getMessage();
		}
		getLog().log(
				new Status(IStatus.ERROR, ID, IStatus.OK, msg, throwable)
		);
	}
}
