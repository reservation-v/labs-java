package ru.moseichuck.mapper;

import ru.moseichuck.dto.CharacterDto;

public class SpeciesGenderCombinationMapper {

    private static final String COMBINATION_PATTERN = "%s - %s";

    public String toCombination(CharacterDto character) {
        return COMBINATION_PATTERN.formatted(character.getSpecies(), character.getGender());
    }
}
