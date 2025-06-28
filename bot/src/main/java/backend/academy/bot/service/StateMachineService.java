package backend.academy.bot.service;

import static backend.academy.bot.dao.state.entity.State.INIT_STATE;
import static backend.academy.bot.dao.state.entity.State.WAITING_FOR_FILTERS;
import static backend.academy.bot.dao.state.entity.State.WAITING_FOR_NOTIFICATION_MODE;
import static backend.academy.bot.dao.state.entity.State.WAITING_FOR_TAGS;
import static java.util.Map.entry;

import backend.academy.bot.dao.state.entity.State;
import backend.academy.bot.dto.UserStateDto;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StateMachineService {

    private static final Map<State, Set<State>> TRANSITIONS = Map.ofEntries(
            entry(INIT_STATE, Set.of(WAITING_FOR_TAGS, WAITING_FOR_NOTIFICATION_MODE)),
            entry(WAITING_FOR_TAGS, Set.of(WAITING_FOR_FILTERS)),
            entry(WAITING_FOR_FILTERS, Set.of(INIT_STATE)),
            entry(WAITING_FOR_NOTIFICATION_MODE, Set.of(INIT_STATE)));

    private boolean isValidTransition(State currentState, State newState) {
        return TRANSITIONS.get(currentState).contains(newState);
    }

    public void transitionUserState(UserStateDto userStateDto, State newState) {
        State currentState = userStateDto.currentState() != null ? userStateDto.currentState() : INIT_STATE;

        if (!isValidTransition(currentState, newState)) {
            return;
        }

        userStateDto.currentState(newState);
    }
}
