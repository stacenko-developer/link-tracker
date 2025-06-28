package backend.academy.scrapper.client;

import static backend.academy.scrapper.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.scrapper.ConstValues.DEFAULT_DIGEST_LINK_UPDATE;
import static backend.academy.scrapper.ConstValues.DEFAULT_IMMEDIATE_LINK_UPDATE;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.service.client.BotClientService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BotClientServiceTest extends CommonClientServiceTest {

    @Autowired
    private BotClientService botServiceClient;

    @Test
    @SneakyThrows
    public void immediateUpdateLinksWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, null);

        setUpWiremockImmediateUpdateToReturnDefaultCorrectResponse();

        ResponseDto<Void> actualResponse = botServiceClient.immediateUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void digestUpdateLinksWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, null);

        setUpWiremockDigestUpdateToReturnDefaultCorrectResponse();

        ResponseDto<Void> actualResponse = botServiceClient.digestUpdate(DEFAULT_DIGEST_LINK_UPDATE);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void immediateUpdateLinksWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        stubFor(immediateUpdateMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<Void> actualResponse = botServiceClient.immediateUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void digestUpdateLinksWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        stubFor(digestUpdateMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<Void> actualResponse = botServiceClient.digestUpdate(DEFAULT_DIGEST_LINK_UPDATE);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }
}
