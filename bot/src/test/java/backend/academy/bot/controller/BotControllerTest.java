package backend.academy.bot.controller;

import static backend.academy.bot.ConstValues.DEFAULT_TG_CHAT_ID;
import static backend.academy.bot.constants.APIConstValues.DIGEST_UPDATE_URL;
import static backend.academy.bot.constants.APIConstValues.IMMEDIATE_UPDATE_URL;
import static backend.academy.bot.constants.APIConstValues.UPDATES_API_BASE_URL;

import backend.academy.bot.dto.linkUpdate.DigestLinkUpdate;
import backend.academy.bot.dto.linkUpdate.EventDto;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import backend.academy.bot.service.BotUpdaterService;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class BotControllerTest extends CommonControllerTest {

    private static final String IMMEDIATE_UPDATE_FULL_URL = UPDATES_API_BASE_URL + IMMEDIATE_UPDATE_URL;
    private static final String DIGEST_UPDATE_FULL_URL = UPDATES_API_BASE_URL + DIGEST_UPDATE_URL;

    private static final ImmediateLinkUpdate DEFAULT_IMMEDIATE_LINK_UPDATE = new ImmediateLinkUpdate(
            1L, URI.create("https://uri.ru"), new EventDto("type", null, "user", 1L, 1L, null), List.of(1L, 2L));
    private static final DigestLinkUpdate DEFAULT_DIGEST_LINK_UPDATE =
            new DigestLinkUpdate(DEFAULT_TG_CHAT_ID, new ArrayList<>());

    @MockitoBean
    private BotUpdaterService botUpdaterService;

    @Test
    @SneakyThrows
    public void sendImmediateUpdateRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doImmediateUpdateRequest);
    }

    @Test
    @SneakyThrows
    public void sendDigestUpdateRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doDigestUpdateRequest);
    }

    @SneakyThrows
    private ResultActions doImmediateUpdateRequest(String ip) {
        return mockMvc.perform(MockMvcRequestBuilders.post(IMMEDIATE_UPDATE_FULL_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(DEFAULT_IMMEDIATE_LINK_UPDATE))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }

    @SneakyThrows
    private ResultActions doDigestUpdateRequest(String ip) {
        return mockMvc.perform(MockMvcRequestBuilders.post(DIGEST_UPDATE_FULL_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(DEFAULT_DIGEST_LINK_UPDATE))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }
}
