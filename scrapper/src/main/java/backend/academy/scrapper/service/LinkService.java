package backend.academy.scrapper.service;

import backend.academy.common.utils.DateTimeUtils;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.service.ChatDaoService;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilterId;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTagId;
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
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkDaoService linkDaoService;
    private final ChatDaoService chatDaoService;
    private final FilterDaoService filterDaoService;
    private final TagDaoService tagDaoService;

    @Transactional(readOnly = true)
    public List<ChatLinkDto> getAllUserTrackingLinks(Long tgChatId, List<String> tagNames) {
        if (chatDaoService.findChatById(tgChatId) == null) {
            throw new ChatNotFoundException(tgChatId);
        }

        return linkDaoService.getAllUserTrackingLinks(tgChatId, tagNames).stream()
                .map(link -> new ChatLinkDto(
                        link.id(),
                        URI.create(link.url()),
                        getChatTags(link, tgChatId).stream()
                                .map(tag -> new TagDto(tag.name()))
                                .toList(),
                        getChatFilters(link, tgChatId).stream()
                                .map(filter -> new FilterDto(filter.key(), filter.value()))
                                .toList()))
                .toList();
    }

    @Transactional
    public ChatLinkDto addLink(Long tgChatId, ChatLinkDto chatLinkDto) {
        Chat chat = chatDaoService.findChatById(tgChatId);

        if (chat == null) {
            throw new ChatNotFoundException(tgChatId);
        }

        if (linkDaoService.findByUrlAndChatId(chatLinkDto.url(), tgChatId) != null) {
            throw new LinkHasAlreadyAddedException(chatLinkDto.url(), tgChatId);
        }

        Link link = linkDaoService.findByUrl(chatLinkDto.url());

        if (link == null) {
            link = new Link();
            link.url(chatLinkDto.url().toString());
            link.lastUpdatedAt(DateTimeUtils.getNowUtc());
            link = linkDaoService.save(link);
        }

        for (TagDto tagDto : chatLinkDto.tags()) {
            Tag tag = tagDaoService.findByName(tagDto.name());

            if (tag == null) {
                tag = new Tag();
                tag.name(tagDto.name());
                tag = tagDaoService.createTag(tag);
            }

            ChatLinkTag chatLinkTag = new ChatLinkTag();

            chatLinkTag.id(new ChatLinkTagId(chat.id(), link.id(), tag.id()));
            chatLinkTag.tag(tag);
            chatLinkTag.chat(chat);
            chatLinkTag.link(link);

            link.chatLinkTags().add(chatLinkTag);
        }

        for (FilterDto filterDto : chatLinkDto.filters()) {
            Filter filter = filterDaoService.findByKeyAndValue(filterDto.key(), filterDto.value());

            if (filter == null) {
                filter = new Filter();
                filter.key(filterDto.key());
                filter.value(filterDto.value());

                filter = filterDaoService.createFilter(filter);
            }

            ChatLinkFilter chatLinkFilter = new ChatLinkFilter();

            chatLinkFilter.id(new ChatLinkFilterId(chat.id(), link.id(), filter.id()));
            chatLinkFilter.filter(filter);
            chatLinkFilter.chat(chat);
            chatLinkFilter.link(link);

            link.chatLinkFilters().add(chatLinkFilter);
        }

        chat.links().add(link);
        link.chats().add(chat);

        Link createdLink = linkDaoService.save(link);

        return new ChatLinkDto(
                createdLink.id(), URI.create(createdLink.url()), chatLinkDto.tags(), chatLinkDto.filters());
    }

    @Transactional
    public ChatLinkDto deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        Chat chat = chatDaoService.findChatById(tgChatId);

        if (chat == null) {
            throw new ChatNotFoundException(tgChatId);
        }

        if (linkDaoService.findByUrlAndChatId(removeLinkRequest.link(), tgChatId) == null) {
            throw new LinkNotFoundException(removeLinkRequest.link(), tgChatId);
        }

        Link link = linkDaoService.findByUrl(removeLinkRequest.link());

        if (link == null) {
            throw new LinkNotFoundException(removeLinkRequest.link(), tgChatId);
        }

        List<Tag> chatTags = getChatTags(link, tgChatId);
        List<Filter> chatFilters = getChatFilters(link, tgChatId);

        chat.links().removeIf(l -> l.id().equals(link.id()));
        link.chats().removeIf(ch -> ch.id().equals(tgChatId));
        link.chatLinkTags().removeIf(chatLinkTag -> chatLinkTag.chat().id().equals(tgChatId));
        link.chatLinkFilters()
                .removeIf(chatLinkFilter -> chatLinkFilter.chat().id().equals(tgChatId));

        linkDaoService.save(link);

        return new ChatLinkDto(
                link.id(),
                URI.create(link.url()),
                chatTags.stream().map(tag -> new TagDto(tag.name())).toList(),
                chatFilters.stream()
                        .map(filter -> new FilterDto(filter.key(), filter.value()))
                        .toList());
    }

    private List<Tag> getChatTags(Link link, Long tgChatId) {
        return link.chatLinkTags().stream()
                .filter(chatLinkTag -> chatLinkTag.chat().id().equals(tgChatId))
                .map(ChatLinkTag::tag)
                .toList();
    }

    private List<Filter> getChatFilters(Link link, Long tgChatId) {
        return link.chatLinkFilters().stream()
                .filter(chatLinkFilter -> chatLinkFilter.chat().id().equals(tgChatId))
                .map(ChatLinkFilter::filter)
                .toList();
    }
}
