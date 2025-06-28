package backend.academy.scrapper.service;

import static backend.academy.scrapper.ConstValues.DEFAULT_ADD_LINK_REQUEST;
import static backend.academy.scrapper.ConstValues.DEFAULT_LINK_ID;
import static backend.academy.scrapper.ConstValues.DEFAULT_URL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.service.ChatDaoService;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.filter.service.FilterDaoService;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.dao.tag.service.TagDaoService;
import backend.academy.scrapper.dto.ChatLinkDto;
import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.dto.TagDto;
import backend.academy.scrapper.dto.request.link.RemoveLinkRequest;
import backend.academy.scrapper.exception.chat.ChatNotFoundException;
import backend.academy.scrapper.exception.link.LinkHasAlreadyAddedException;
import backend.academy.scrapper.exception.link.LinkNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    private static final long DEFAULT_CHAT_ID = 1;

    @InjectMocks
    private LinkService linkService;

    @Mock
    private LinkDaoService linkDaoService;

    @Mock
    private ChatDaoService chatDaoService;

    @Mock
    private TagDaoService tagDaoService;

    @Mock
    private FilterDaoService filterDaoService;

    @Test
    public void addLinkWithCorrectArguments_ShouldAddLink() {
        String tagName = "Название тэга";
        String filterKey = "Ключ фильтра";
        String filterValue = "Значение фильтра";

        Tag tag = new Tag();
        tag.name(tagName);

        Filter filter = new Filter();
        filter.key(filterKey);
        filter.value(filterValue);

        Link linkToAdd = new Link();
        linkToAdd.id(DEFAULT_LINK_ID);
        linkToAdd.url(DEFAULT_URL.toString());

        Link createdLink = new Link();

        Chat chat = new Chat();
        chat.id(DEFAULT_CHAT_ID);

        createdLink.chats(new ArrayList<>(List.of(chat)));
        createdLink.id(DEFAULT_LINK_ID);
        createdLink.url(DEFAULT_URL.toString());

        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(chat);
        when(linkDaoService.findByUrlAndChatId(DEFAULT_URL, DEFAULT_CHAT_ID)).thenReturn(null);
        when(linkDaoService.findByUrl(DEFAULT_URL)).thenReturn(linkToAdd);
        when(linkDaoService.save(any())).thenReturn(createdLink);

        when(tagDaoService.findByName(tagName)).thenReturn(tag);
        when(filterDaoService.findByKeyAndValue(filterKey, filterValue)).thenReturn(filter);

        ChatLinkDto actualChatDtoRequest = new ChatLinkDto(
                null, DEFAULT_URL, List.of(new TagDto(tagName)), List.of(new FilterDto(filterKey, filterValue)));

        ChatLinkDto expectedChatLinkDto = new ChatLinkDto(
                DEFAULT_LINK_ID,
                DEFAULT_URL,
                List.of(new TagDto(tagName)),
                List.of(new FilterDto(filterKey, filterValue)));

        ChatLinkDto actualChatLinkDto = linkService.addLink(DEFAULT_CHAT_ID, actualChatDtoRequest);

        Assertions.assertEquals(expectedChatLinkDto, actualChatLinkDto);
    }

    @Test
    public void addDuplicationLink_ShouldThrowLinkHasAlreadyAddedException() {
        Chat chat = new Chat();
        chat.id(DEFAULT_CHAT_ID);

        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(chat);
        when(linkDaoService.findByUrlAndChatId(DEFAULT_URL, DEFAULT_CHAT_ID)).thenReturn(new Link());

        assertThatThrownBy(() -> {
                    linkService.addLink(DEFAULT_CHAT_ID, DEFAULT_ADD_LINK_REQUEST.toChatLinkDto());
                })
                .isInstanceOf(LinkHasAlreadyAddedException.class)
                .satisfies(exception -> {
                    final LinkHasAlreadyAddedException ex = (LinkHasAlreadyAddedException) exception;

                    String expectedDescription = String.format(
                            ExceptionDescriptionValues.LINK_HAS_ALREADY_ADDED_EXCEPTION_DESCRIPTION,
                            DEFAULT_URL,
                            DEFAULT_CHAT_ID);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.LINK_HAS_ALREADY_ADDED_EXCEPTION_MESSAGE);
    }

    @Test
    public void addLinkForNotRegisteredChat_ShouldThrowChatNotFoundException() {
        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(null);

        assertThatThrownBy(() -> {
                    linkService.addLink(DEFAULT_CHAT_ID, DEFAULT_ADD_LINK_REQUEST.toChatLinkDto());
                })
                .isInstanceOf(ChatNotFoundException.class)
                .satisfies(exception -> {
                    final ChatNotFoundException ex = (ChatNotFoundException) exception;

                    String expectedDescription = String.format(
                            ExceptionDescriptionValues.CHAT_NOT_FOUND_EXCEPTION_DESCRIPTION, DEFAULT_CHAT_ID);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    public void deleteLinkWithCorrectArguments_ShouldDeleteLink() {
        Long linkId = 1L;
        Chat chat = new Chat();
        chat.id(DEFAULT_CHAT_ID);
        List<Chat> chats = new ArrayList<>();
        chats.add(chat);

        Link linkToDelete = new Link();
        linkToDelete.id(linkId);
        linkToDelete.url(DEFAULT_URL.toString());
        linkToDelete.chats(chats);

        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(chat);
        when(linkDaoService.findByUrl(DEFAULT_URL)).thenReturn(linkToDelete);
        when(linkDaoService.findByUrlAndChatId(DEFAULT_URL, DEFAULT_CHAT_ID)).thenReturn(linkToDelete);
        when(linkDaoService.save(linkToDelete)).thenReturn(linkToDelete);

        ChatLinkDto expectedChatLinkDto = new ChatLinkDto(linkId, DEFAULT_URL, new ArrayList<>(), new ArrayList<>());
        ChatLinkDto actualChatLinkDto = linkService.deleteLink(DEFAULT_CHAT_ID, new RemoveLinkRequest(DEFAULT_URL));

        Assertions.assertEquals(expectedChatLinkDto, actualChatLinkDto);
    }

    @Test
    public void deleteNotAddedLink_ShouldThrowLinkNotFoundException() {
        Chat chat = new Chat();
        chat.id(DEFAULT_CHAT_ID);

        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(chat);
        when(linkDaoService.findByUrlAndChatId(DEFAULT_URL, DEFAULT_CHAT_ID)).thenReturn(null);

        assertThatThrownBy(() -> {
                    linkService.deleteLink(DEFAULT_CHAT_ID, new RemoveLinkRequest(DEFAULT_URL));
                })
                .isInstanceOf(LinkNotFoundException.class)
                .satisfies(exception -> {
                    final LinkNotFoundException ex = (LinkNotFoundException) exception;

                    String expectedDescription = String.format(
                            ExceptionDescriptionValues.LINK_NOT_FOUND_EXCEPTION_DESCRIPTION,
                            DEFAULT_URL,
                            DEFAULT_CHAT_ID);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.LINK_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Test
    public void deleteLinkForNotRegisteredChat_ShouldThrowChatNotFoundException() {
        when(chatDaoService.findChatById(DEFAULT_CHAT_ID)).thenReturn(null);

        assertThatThrownBy(() -> {
                    linkService.deleteLink(DEFAULT_CHAT_ID, new RemoveLinkRequest(DEFAULT_URL));
                })
                .isInstanceOf(ChatNotFoundException.class)
                .satisfies(exception -> {
                    final ChatNotFoundException ex = (ChatNotFoundException) exception;

                    String expectedDescription = String.format(
                            ExceptionDescriptionValues.CHAT_NOT_FOUND_EXCEPTION_DESCRIPTION, DEFAULT_CHAT_ID);
                    String actualDescription = ex.description();

                    Assertions.assertEquals(expectedDescription, actualDescription);
                })
                .hasMessageContaining(ExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
