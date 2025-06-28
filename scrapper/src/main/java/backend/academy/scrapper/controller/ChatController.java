package backend.academy.scrapper.controller;

import static backend.academy.common.constants.ExceptionTextValues.INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION;
import static backend.academy.common.constants.HttpStatusCodesConstValues.BAD_REQUEST;
import static backend.academy.common.constants.HttpStatusCodesConstValues.NOT_FOUND;
import static backend.academy.common.constants.HttpStatusCodesConstValues.OK;
import static backend.academy.scrapper.constants.APIConstValues.TG_CHAT_API_FULL_URL;
import static backend.academy.scrapper.constants.exception.ExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE;

import backend.academy.common.dto.ApiErrorResponse;
import backend.academy.scrapper.dto.request.chat.ChatSettingsRequest;
import backend.academy.scrapper.dto.response.chat.ChatResponse;
import backend.academy.scrapper.manager.ChatManager;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = TG_CHAT_API_FULL_URL)
public class ChatController {

    private final ChatManager chatManager;

    @PostMapping
    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = OK, description = "Чат зарегистрирован"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION)
            })
    public ResponseEntity<Void> registerChat(@PathVariable Long id) {
        chatManager.registerChat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Обновить настройки чата")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ChatResponse.class)),
                        description = "Настройки чата обновлены"),
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
    public ResponseEntity<ChatResponse> updateChatSettings(
            @PathVariable Long id, @RequestBody @Valid ChatSettingsRequest chatSettingsRequest) {
        return new ResponseEntity<>(chatManager.updateChatSettings(id, chatSettingsRequest), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Получить настройки чата")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ChatResponse.class)),
                        description = "Возвращает настройки чата"),
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
    public ResponseEntity<ChatResponse> getChatById(@PathVariable Long id) {
        return new ResponseEntity<>(chatManager.getChatById(id), HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Удалить чат")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = OK, description = "Чат успешно удалён"),
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
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatManager.deleteChat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
