package fr.fms.processors;

import fr.fms.entities.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User item) throws Exception {
        return item;
    }
}
