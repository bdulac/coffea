package net.sourceforge.coffea.edit;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class Icons {

	/**
	 * Default (visibility ) property icon creation
	 * @return Created icon
	 */
	public static Image createDefaultPropertyIcon() {
		ImageDescriptor img = JavaPluginImages.DESC_FIELD_DEFAULT;
		return img.createImage();
	}
	
	/**
	 * Public property icon creation
	 * @return Created icon
	 */
	public static Image createPublicPropertyIcon() {
		ImageDescriptor img = JavaPluginImages.DESC_FIELD_PUBLIC;
		return img.createImage();
	}
	
	/**
	 * Public property icon creation
	 * @return Created icon
	 */
	public static Image createProtectedPropertyIcon() {
		ImageDescriptor img = JavaPluginImages.DESC_FIELD_PROTECTED;
		return img.createImage();
	}
	
	/**
	 * Private property icon creation
	 * @return Created icon
	 */
	public static Image createPrivatePropertyIcon() {
		ImageDescriptor img = JavaPluginImages.DESC_FIELD_PRIVATE;
		return img.createImage();
	}
	
}
