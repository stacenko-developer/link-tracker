package backend.academy.scrapper.controller;

import static backend.academy.common.constants.HttpStatusCodesConstValues.OK;
import static backend.academy.scrapper.constants.APIConstValues.GET_NOTIFICATION_MODES_URL;
import static backend.academy.scrapper.constants.APIConstValues.TG_CHAT_SETTINGS_API_BASE_URL;

import backend.academy.scrapper.dto.NotificationModeDto;
import backend.academy.scrapper.service.ChatSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = TG_CHAT_SETTINGS_API_BASE_URL)
public class ChatSettingsController {

    private final ChatSettingsService chatSettingsService;

    @GetMapping(value = GET_NOTIFICATION_MODES_URL)
    @Operation(
            summary = "Получить способы отправки уведомлений",
            description = "Получение способов отправки уведомлений")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = OK,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        array =
                                                @ArraySchema(
                                                        schema = @Schema(implementation = NotificationModeDto.class))),
                        description = "Возвращает способы отправки уведомлений")
            })
    public ResponseEntity<?> getNotificationModes() {
        return new ResponseEntity<>(chatSettingsService.getNotificationModes(), HttpStatus.OK);
    }
}
