package ru.moseichuck.service;

import ru.moseichuck.dto.CharacterDto;
import ru.moseichuck.mapper.SpeciesGenderCombinationMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeciesGenderCombinationService {

    private final SpeciesGenderCombinationMapper combinationMapper;

    public SpeciesGenderCombinationService(SpeciesGenderCombinationMapper combinationMapper) {
        this.combinationMapper = combinationMapper;
    }

    public Set<String> collectCombinations(List<CharacterDto> characters) {
        Set<String> combinations = new HashSet<>();
        for (CharacterDto character : characters) {
            combinations.add(combinationMapper.toCombination(character));
        }
        return combinations;
    }
}
