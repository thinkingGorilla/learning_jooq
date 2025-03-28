package me.thinking_gorilla.jooqfirstlook.actor;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActorFilmographySearchCondition {

    private final String actorName;
    private final String filmTitle;
}
