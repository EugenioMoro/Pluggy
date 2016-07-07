package botContexts;

import org.telegram.telegrambots.api.objects.Update;

public abstract class context {
	
	private static context instance;
	
	
	public void work(Update update) {
	}
	
	public void abort() {
	}

	public static context getInstance() {
		return instance;
	}

	public void setInstance(context instance) {
		context.instance = instance;
	}




}
