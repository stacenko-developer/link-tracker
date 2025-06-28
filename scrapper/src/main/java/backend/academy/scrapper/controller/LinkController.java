package backend.academy.scrapper.controller;

import static backend.academy.common.constants.ExceptionTextValues.INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION;
import static backend.academy.common.constants.HttpStatusCodesConstValues.BAD_REQUEST;
import static backend.academy.common.constants.HttpStatusCodesConstValues.NOT_FOUND;
import static backend.academy.common.constants.HttpStatusCodesConstValues.OK;
import static backend.academy.scrapper.constants.APIConstValues.LINKS_API_BASE_URL;
import static backend.academy.scrapper.constants.APIConstValues.SEARCH_LINKS_URL;
import static backend.academy.scrapper.constants.APIConstValues.TG_CHAT_ID_HEADER;
import static backend.academy.scrapper.constants.exception.ExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE;
import static backend.academy.scrapper.constants.exception.ExceptionMessageValues.LINK_NOT_FOUND_EXCEPTION_MESSAGE;

import backend.academy.common.dto.ApiErrorResponse;
import backend.academy.scrapper.dto.request.link.AddLinkRequest;
import backend.academy.scrapper.dto.request.link.FindUserLinksRequest;
import backend.academy.scrapper.dto.request.link.RemoveLinkRequest;
import backend.academy.scrapper.dto.response.link.LinkResponse;
import backend.academy.scrapper.dto.response.link.ListLinksResponse;
import backend.academy.scrapper.manager.LinkManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = LINKS_API_BASE_URL)
public class LinkController {

    private final LinkManager linkManager;

    @PostMapping(SEARCH_LINKS_URL)
    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ListLinksResponse.class)),
                        description = "Ссылки успешно получены"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION),
                @ApiResponse(
                        responseCode = NOT_FOUND,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = CHAT_NOT_FOUND_EXCEPTION_MESSAGE)
            })
    public ResponseEntity<ListLinksResponse> getAllLinks(
            @RequestBody @Valid FindUserLinksRequest findUserLinksRequest) {
        return new ResponseEntity<>(linkManager.getAllUserTrackingLinks(findUserLinksRequest), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = LinkResponse.class)),
                        description = "Ссылка успешно добавлена"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION),
                @ApiResponse(
                        responseCode = NOT_FOUND,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = CHAT_NOT_FOUND_EXCEPTION_MESSAGE)
            })
    public ResponseEntity<LinkResponse> addLink(
            @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody @Valid AddLinkRequest addLinkRequest) {
        return new ResponseEntity<>(linkManager.addLink(tgChatId, addLinkRequest), HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = LinkResponse.class)),
                        description = "Ссылка успешно убрана"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION),
                @ApiResponse(
                        responseCode = NOT_FOUND,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = LINK_NOT_FOUND_EXCEPTION_MESSAGE)
            })
    public ResponseEntity<LinkResponse> deleteLink(
            @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return new ResponseEntity<>(linkManager.deleteLink(tgChatId, removeLinkRequest), HttpStatus.OK);
    }
}
