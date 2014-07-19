package net.sourceforge.coffea.uml2;

import java.util.ResourceBundle;

/** Resources tool */
public class Resources {

	/** Messages resource */
	private static ResourceBundle messages = 
		ResourceBundle.getBundle("Messages");
	
	/** Parameters resource */
	private static ResourceBundle parameters = 
		ResourceBundle.getBundle("Parameters");
	
	/** Code constants resource */
	private static ResourceBundle codeConstants = 
		ResourceBundle.getBundle("CodeConstants");
	
	/**
	 * Returns {@link #messages}
	 * @return Value of {@link #messages}
	 */
	public static synchronized ResourceBundle getMessages() {
		return messages;
	}
	
	/**
	 * Returns {@link #parameters}
	 * @return Value of {@link #parameters}
	 */
	public static synchronized ResourceBundle getParameters() {
		return parameters;
	}
	
	/**
	 * Returns {@link #codeConstants}
	 * @return Value of {@link #codeConstants}
	 */
	public static synchronized ResourceBundle getCodeConstants() {
		return codeConstants;
	}
	
	/**
	 * Returns a message given its key
	 * @param key
	 * Message key
	 * @return Message
	 */
	public static String getMessage(String key) {
		return getMessages().getString(key);
	}
	
	/**
	 * Returns a parameter given its key
	 * @param key
	 * Message key
	 * @return Parameter
	 */
	public static String getParameter(String key) {
		return getParameters().getString(key);
	}
	
	/**
	 * Returns a code constant given its key
	 * @param key
	 * Message key
	 * @return Code constant
	 */
	public static String getCodeConstant(String key) {
		return getCodeConstants().getString(key);
	}
}
