package com.zorge_string;

import javax.swing.JOptionPane;

public class Utils {

	/**
	 * Message box with error message.
	 * 
	 * @param message
	 */
	static public void MessageBox_Error(final String message) {	
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}	
}
