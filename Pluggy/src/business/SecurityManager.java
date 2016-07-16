package business;

import org.telegram.telegrambots.api.objects.Update;

/*
 * This class manages security. Authentication relies on external telegram authentication process.
 * The system trusts every update and it could be vulnerable to spoofing at least
 */

public class SecurityManager {
	
	private static SecurityManager instance;
	final public static String ADMIN_LEVEL="adminlevel";
	final public static String AUTHORIZED_LEVEL="authorizedlevel";
	
	public static SecurityManager getInstance(){
		if (instance == null){
			instance= new SecurityManager();
		}
		return instance;
	}

	public Boolean securityCheck(Update u, String secLevel){
		switch (secLevel){
		case ADMIN_LEVEL:
			return UserManager.getInstance().getUserById(u.getMessage().getChatId()).getIsAdmin();
		case AUTHORIZED_LEVEL:
			return UserManager.getInstance().getUserById(u.getMessage().getChatId()).getIsAuth();
		}
		System.out.println("User not authorized");
		return false;

	}


}
