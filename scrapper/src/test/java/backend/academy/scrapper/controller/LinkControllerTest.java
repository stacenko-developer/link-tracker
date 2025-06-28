package backend.academy.scrapper.controller;

import static backend.academy.scrapper.ConstValues.DEFAULT_URL;
import static backend.academy.scrapper.constants.APIConstValues.LINKS_API_BASE_URL;
import static backend.academy.scrapper.constants.APIConstValues.SEARCH_LINKS_URL;

import backend.academy.scrapper.dto.request.link.AddLinkRequest;
import backend.academy.scrapper.dto.request.link.FindUserLinksRequest;
import backend.academy.scrapper.dto.request.link.RemoveLinkRequest;
import backend.academy.scrapper.manager.LinkManager;
import java.util.ArrayList;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class LinkControllerTest extends CommonControllerTest {

    private static final String TG_CHAT_ID_HEADER_NAME = "Tg-Chat-Id";

    private static final String SEARCH_LINKS_FULL_URL = LINKS_API_BASE_URL + SEARCH_LINKS_URL;

    @MockitoBean
    private LinkManager linkManager;

    @Test
    @SneakyThrows
    public void sendGetAllLinksRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doGetAllLinksRequest);
    }

    @Test
    @SneakyThrows
    public void sendAddLinkRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doAddLinkRequest);
    }

    @Test
    @SneakyThrows
    public void sendRemoveLinkRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doRemoveLinkRequest);
    }

    @SneakyThrows
    private ResultActions doGetAllLinksRequest(String ip) {
        FindUserLinksRequest findUserLinksRequest = new FindUserLinksRequest(DEFAULT_CHAT_ID, null);

        return mockMvc.perform(MockMvcRequestBuilders.post(SEARCH_LINKS_FULL_URL)
                .contentType(DEFAULT_CONTENT_TYPE)
                .content(objectMapper.writeValueAsBytes(findUserLinksRequest))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }

    @SneakyThrows
    private ResultActions doAddLinkRequest(String ip) {
        AddLinkRequest addLinkRequest = new AddLinkRequest(DEFAULT_URL, new ArrayList<>(), new ArrayList<>());

        return mockMvc.perform(MockMvcRequestBuilders.post(LINKS_API_BASE_URL)
                .header(TG_CHAT_ID_HEADER_NAME, DEFAULT_CHAT_ID)
                .contentType(DEFAULT_CONTENT_TYPE)
                .content(objectMapper.writeValueAsBytes(addLinkRequest))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }

    @SneakyThrows
    private ResultActions doRemoveLinkRequest(String ip) {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(DEFAULT_URL);

        return mockMvc.perform(MockMvcRequestBuilders.delete(LINKS_API_BASE_URL)
                .header(TG_CHAT_ID_HEADER_NAME, DEFAULT_CHAT_ID)
                .contentType(DEFAULT_CONTENT_TYPE)
                .content(objectMapper.writeValueAsBytes(removeLinkRequest))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }
}
