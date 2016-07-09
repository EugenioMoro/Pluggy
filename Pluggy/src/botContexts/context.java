package botContexts;

import org.telegram.telegrambots.api.objects.Update;

public interface context {

	void abort();
	void work(Update update);
	
}
