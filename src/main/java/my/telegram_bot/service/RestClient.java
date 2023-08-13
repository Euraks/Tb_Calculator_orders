package my.telegram_bot.service;

import my.telegram_bot.model.NewPeople;
import my.telegram_bot.model.Page;
import my.telegram_bot.model.People;
import my.telegram_bot.model.SingleUserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RestClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<People> getPeopleByPageNumber(int pageNumber) {
        String url = "https://reqres.in/api/users?page=";
        Page page = restTemplate.getForObject( url + pageNumber, Page.class );
        assert page != null;
        return page.getData();
    }

    public People getPeopleById(String peopleId) {
        String url = "https://reqres.in/api/users/";
        SingleUserDTO singleUserDTO = restTemplate.getForObject( url + peopleId, SingleUserDTO.class );
        assert singleUserDTO != null;
        return singleUserDTO.getPeople();
    }

    public void createUser(String s) {
        String url = "https://reqres.in/api/users";
        HttpEntity<NewPeople> request = new HttpEntity<NewPeople>( new NewPeople( s, "no Job" ) );
        restTemplate.postForObject( url, request, String.class );
    }
}
