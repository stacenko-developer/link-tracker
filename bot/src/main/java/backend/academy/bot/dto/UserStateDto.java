package backend.academy.bot.dto;

import backend.academy.bot.dao.state.entity.State;
import backend.academy.bot.dao.state.entity.UserState;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UserStateDto {
    private Long id;
    private State currentState;
    private String link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public UserState toUserState() {
        UserState userState = new UserState();

        userState.id(id);
        userState.currentState(currentState);
        userState.link(link);
        userState.tags(tags);
        userState.filters(filters);

        return userState;
    }
}
