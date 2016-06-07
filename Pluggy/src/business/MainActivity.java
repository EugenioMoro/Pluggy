package business;

import java.io.IOException;

public class MainActivity {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento\n");
		
		//Load configuration
		//initialize session
		Session.currentSession();
		
		
	

	}

	public static void Abort(Boolean isGraceful){
		System.exit((isGraceful) ? 1 : 0);
	}
	
}