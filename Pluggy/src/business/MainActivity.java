package business;

public class MainActivity {

	public static void main(String[] args) throws InterruptedException {
		Session.currentSession();
	

	}

	public static void Abort(Boolean isGraceful){
		System.exit((isGraceful) ? 1 : 0);
	}
	
}