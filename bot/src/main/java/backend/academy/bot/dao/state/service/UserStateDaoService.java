package backend.academy.bot.dao.state.service;

import backend.academy.bot.dao.state.entity.UserState;
import backend.academy.bot.dao.state.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateDaoService {

    private final UserStateRepository userStateRepository;

    public UserState findUserStateByUserId(long userId) {
        return userStateRepository.findUserStateByUserId(userId);
    }

    public void upgradeUserState(long userId, UserState state) {
        userStateRepository.upgradeUserState(userId, state);
    }

    public void deleteUserState(long userId) {
        userStateRepository.deleteUserState(userId);
    }
}
