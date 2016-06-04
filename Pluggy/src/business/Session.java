package business;



public class Session {

	public final static String VERSION="0.1 alpha";
	public final static String TOKEN="200488739:AAHjKEu5eG8UnqG4bItg3GCR_quW9X02MSk";
	
	private static Session currentSession;
	
	
	public static Session currentSession() throws InterruptedException{
		if (currentSession==null){
			currentSession = new Session();
		}
		return currentSession;
		}
	

	public Session() throws InterruptedException{
		
		System.out.println("Pluggy - Smart Plug Software\nVersion:" + Session.VERSION + "\nEugenio Moro @unisalento");
		System.out.println("Token: " + TOKEN);
		System.out.println("\nInitializing bot...");
		
		
		
	}

}