package backend.academy.bot.dto.command;

import backend.academy.bot.dto.UserStateDto;
import com.pengrad.telegrambot.request.SendMessage;

public record CommandResponseDto(SendMessage sendMessage, UserStateDto userStateDto) {}
