package backend.academy.bot.service;

import backend.academy.bot.dao.state.entity.UserState;
import backend.academy.bot.dao.state.service.UserStateDaoService;
import backend.academy.bot.dto.UserStateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {

    private final UserStateDaoService userStateDaoService;

    public UserStateDto findUserStateByUserId(long userId) {
        UserState userState = userStateDaoService.findUserStateByUserId(userId);

        if (userState == null) {
            return null;
        }

        return userState.toUserStateDto();
    }

    public void upgradeUserState(long userId, UserStateDto userStateDto) {
        if (userStateDto == null) {
            return;
        }

        userStateDaoService.upgradeUserState(userId, userStateDto.toUserState());
    }

    public void deleteUserState(long userId) {
        userStateDaoService.deleteUserState(userId);
    }
}
