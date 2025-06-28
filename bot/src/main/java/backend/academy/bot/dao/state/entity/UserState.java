package backend.academy.bot.dao.state.entity;

import backend.academy.bot.dto.UserStateDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UserState {
    private Long id;
    private State currentState;
    private String link;
    private List<String> tags = new ArrayList<>();
    private List<String> filters = new ArrayList<>();

    public UserStateDto toUserStateDto() {
        UserStateDto userStateDto = new UserStateDto();

        userStateDto.id(id);
        userStateDto.currentState(currentState);
        userStateDto.link(link);
        userStateDto.tags(tags);
        userStateDto.filters(filters);

        return userStateDto;
    }
}
