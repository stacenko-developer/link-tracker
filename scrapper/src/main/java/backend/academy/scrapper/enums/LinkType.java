package backend.academy.scrapper.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum LinkType {
    GITHUB,
    STACKOVERFLOW;

    public static LinkType getLinkType(String link) {
        return Arrays.stream(values())
                .filter(linkType -> link.contains(linkType.name().toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
