package backend.academy.bot.dao.state.repository;

import backend.academy.bot.dao.state.entity.UserState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class UserStateRepository {

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();

    public UserState findUserStateByUserId(long userId) {
        return userStates.get(userId);
    }

    public void upgradeUserState(long userId, UserState state) {
        userStates.put(userId, state);
    }

    public void deleteUserState(long userId) {
        userStates.remove(userId);
    }
}
