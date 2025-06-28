package backend.academy.scrapper.controller;

import static backend.academy.scrapper.constants.APIConstValues.TG_CHAT_API_BASE_URL;

import backend.academy.scrapper.dto.request.chat.ChatSettingsRequest;
import backend.academy.scrapper.manager.ChatManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ChatControllerTest extends CommonControllerTest {

    private static final String CHAT_URI_TEMPLATE = String.format("%s/%s", TG_CHAT_API_BASE_URL, DEFAULT_CHAT_ID);

    @MockitoBean
    private ChatManager chatManager;

    @Test
    @SneakyThrows
    public void sendRegisterChatRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doRegisterChatRequest);
    }

    @Test
    @SneakyThrows
    public void sendUpdateChatSettingsRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doUpdateChatSettingsRequest);
    }

    @Test
    @SneakyThrows
    public void sendGetChatRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doGetChatRequest);
    }

    @Test
    @SneakyThrows
    public void sendDeleteChatRequestManyTimes_ShouldReturnTooManyRequestsCode() {
        testRateLimitingProcess(this::doDeleteChatRequest);
    }

    @SneakyThrows
    private ResultActions doRegisterChatRequest(String ip) {
        return mockMvc.perform(MockMvcRequestBuilders.post(CHAT_URI_TEMPLATE).with(request -> {
            request.setRemoteAddr(ip);
            return request;
        }));
    }

    @SneakyThrows
    private ResultActions doUpdateChatSettingsRequest(String ip) {
        ChatSettingsRequest chatSettingsRequest = new ChatSettingsRequest("code");

        return mockMvc.perform(MockMvcRequestBuilders.put(CHAT_URI_TEMPLATE)
                .contentType(DEFAULT_CONTENT_TYPE)
                .content(objectMapper.writeValueAsBytes(chatSettingsRequest))
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                }));
    }

    @SneakyThrows
    private ResultActions doGetChatRequest(String ip) {
        return mockMvc.perform(MockMvcRequestBuilders.get(CHAT_URI_TEMPLATE).with(request -> {
            request.setRemoteAddr(ip);
            return request;
        }));
    }

    @SneakyThrows
    private ResultActions doDeleteChatRequest(String ip) {
        return mockMvc.perform(MockMvcRequestBuilders.delete(CHAT_URI_TEMPLATE).with(request -> {
            request.setRemoteAddr(ip);
            return request;
        }));
    }
}
