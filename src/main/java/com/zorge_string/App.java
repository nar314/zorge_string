package com.zorge_string;

public class App {

	public static void main(String[] args) {
        
		try {
	        MainWindow mw = new MainWindow();
	        mw.show();
    	}
    	catch(Exception e) {
    		Utils.MessageBox_Error(e.getMessage());
    	}
    }
}
