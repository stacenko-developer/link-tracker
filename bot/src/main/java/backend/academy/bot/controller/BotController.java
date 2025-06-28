package backend.academy.bot.controller;

import static backend.academy.bot.constants.APIConstValues.DIGEST_UPDATE_URL;
import static backend.academy.bot.constants.APIConstValues.IMMEDIATE_UPDATE_URL;
import static backend.academy.bot.constants.APIConstValues.UPDATES_API_BASE_URL;
import static backend.academy.common.constants.ExceptionTextValues.INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION;
import static backend.academy.common.constants.HttpStatusCodesConstValues.BAD_REQUEST;
import static backend.academy.common.constants.HttpStatusCodesConstValues.OK;

import backend.academy.bot.dto.linkUpdate.DigestLinkUpdate;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import backend.academy.bot.service.BotUpdaterService;
import backend.academy.common.dto.ApiErrorResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = UPDATES_API_BASE_URL)
public class BotController {

    private final BotUpdaterService botUpdaterService;

    @PostMapping(IMMEDIATE_UPDATE_URL)
    @Operation(
            summary = "Отправить обновление, которое приходит сразу при обнаружении изменений по отслеживаемой ссылке")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = OK, description = "Обновление обработано"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION)
            })
    public ResponseEntity<Void> immediateUpdate(@RequestBody @Valid ImmediateLinkUpdate immediateLinkUpdate) {
        botUpdaterService.immediatelyUpdate(immediateLinkUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(DIGEST_UPDATE_URL)
    @Operation(summary = "Отправить обновления в виде дайджеста по отслеживаемым ссылкам")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = OK, description = "Обновления обработаны"),
                @ApiResponse(
                        responseCode = BAD_REQUEST,
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)),
                        description = INCORRECT_ARGUMENTS_EXCEPTION_DESCRIPTION)
            })
    public ResponseEntity<Void> digestUpdate(@RequestBody @Valid DigestLinkUpdate digestLinkUpdate) {
        botUpdaterService.digestUpdate(digestLinkUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
