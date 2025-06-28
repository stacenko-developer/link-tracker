package backend.academy.scrapper.client.github;

import static backend.academy.scrapper.constants.APIConstValues.AUTHORIZATION_HEADER;
import static backend.academy.scrapper.constants.APIConstValues.GET_REPOSITORIES_URL;

import backend.academy.scrapper.client.github.dto.GithubEventDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface GithubClient {

    @GetExchange(GET_REPOSITORIES_URL)
    ResponseEntity<List<GithubEventDto>> getRepositoryEvents(
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @PathVariable String owner,
            @PathVariable String repository);
}
