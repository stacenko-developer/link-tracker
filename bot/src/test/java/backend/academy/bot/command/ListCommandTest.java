package backend.academy.bot.command;

import static backend.academy.bot.ConstValues.DEFAULT_TG_CHAT_ID;
import static org.mockito.Mockito.when;

import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import backend.academy.bot.configuration.command.ListCommandProperties;
import backend.academy.bot.dto.command.CommandRequestDto;
import backend.academy.bot.dto.command.CommandResponseDto;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class ListCommandTest extends CommonCommandTest {

    private static final String LIST_COMMAND = "/list";

    @InjectMocks
    private ListCommand listCommand;

    @Mock
    private ScrapperClientService scrapperServiceClient;

    @Mock
    private ListCommandProperties listCommandProperties;

    @Test
    public void processListCommandWithCorrectArguments_ShouldCorrectlyProcess() {
        String firstUrl = "https://github.com/owner1/repo1";
        List<String> firstTags = List.of("тег1", "тег2");
        List<String> firstFilters = List.of("event:CreateEvent", "user:login1");

        String secondUrl = "https://github.com/owner2/repo2";
        List<String> secondTags = List.of("тег3", "тег4");
        List<String> secondFilters = List.of("event:PushEvent", "user:login2");

        List<LinkResponse> links = List.of(
                new LinkResponse(1L, URI.create(firstUrl), firstTags, firstFilters),
                new LinkResponse(1L, URI.create(secondUrl), secondTags, secondFilters));
        ResponseDto<ListLinksResponse> responseDto =
                new ResponseDto<>(new ListLinksResponse(links, links.size()), null);

        String header = "Список отслеживаемых ссылок";
        String format = "%n%nCcылка: %s%nТеги: %s%nФильтры: %s";

        when(scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null))
                .thenReturn(responseDto);
        when(listCommandProperties.header()).thenReturn(header);
        when(listCommandProperties.format()).thenReturn(format);

        CommandResponseDto commandResponseDto =
                listCommand.process(new CommandRequestDto(DEFAULT_TG_CHAT_ID, LIST_COMMAND, null));
        String[] actualMessage = getText(commandResponseDto.sendMessage()).split("\n");

        Assertions.assertTrue(actualMessage[2].contains(firstUrl));
        Assertions.assertTrue(actualMessage[3].contains(String.join(", ", firstTags)));
        Assertions.assertTrue(actualMessage[4].contains(String.join(", ", firstFilters)));

        Assertions.assertTrue(actualMessage[6].contains(secondUrl));
        Assertions.assertTrue(actualMessage[7].contains(String.join(", ", secondTags)));
        Assertions.assertTrue(actualMessage[8].contains(String.join(", ", secondFilters)));
    }

    @Test
    public void processListCommandWithNoLinks_ShouldReturnEmptyListMessage() {
        ResponseDto<ListLinksResponse> responseDto =
                new ResponseDto<>(new ListLinksResponse(new ArrayList<>(), 0), null);
        String expectedEmptyListMessage = "Список отслеживаемых ссылок пуст";

        when(scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null))
                .thenReturn(responseDto);
        when(listCommandProperties.trackingLinksNotFound()).thenReturn(expectedEmptyListMessage);

        CommandResponseDto commandResponseDto =
                listCommand.process(new CommandRequestDto(DEFAULT_TG_CHAT_ID, LIST_COMMAND, null));
        String actualMessage = getText(commandResponseDto.sendMessage());
        ;

        Assertions.assertEquals(expectedEmptyListMessage, actualMessage);
    }
}
