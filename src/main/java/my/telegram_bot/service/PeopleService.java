package my.telegram_bot.service;

import my.telegram_bot.model.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PeopleService {

    @Autowired
    private RestClient restClient;

    public List<People> getPeople(int pageNumber) {
        return restClient.getPeopleByPageNumber( pageNumber );
    }

    public People getPeopleForId(String peopleId) {
        return restClient.getPeopleById( peopleId );
    }

    public void createUser(String s) {
        restClient.createUser( s );
    }
}
