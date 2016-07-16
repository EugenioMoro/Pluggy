package botContexts;

import org.telegram.telegrambots.api.objects.Update;

/*
 * See documentation
 */

public interface context {

	void abort();
	void work(Update update);
	
}
