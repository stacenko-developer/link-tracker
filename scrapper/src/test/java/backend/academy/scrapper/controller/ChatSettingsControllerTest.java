package backend.academy.scrapper.controller;

import static backend.academy.scrapper.constants.APIConstValues.GET_NOTIFICATION_MODES_URL;
import static backend.academy.scrapper.constants.APIConstValues.TG_CHAT_SETTINGS_API_BASE_URL;

import backend.academy.scrapper.service.ChatSettingsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ChatSettingsControllerTest extends CommonControllerTest {

    private static final String GET_NOTIFICATION_MODES_FULL_URL =
            TG_CHAT_SETTINGS_API_BASE_URL + GET_NOTIFICATION_MODES_URL;

    @MockitoBean
    private ChatSettingsService chatSettingsService;

    @Test
    @SneakyThrows
    public void sendGetNotificationModesRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doGetNotificationModesRequest);
    }

    @SneakyThrows
    private ResultActions doGetNotificationModesRequest(String ip) {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(GET_NOTIFICATION_MODES_FULL_URL).with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }
}
