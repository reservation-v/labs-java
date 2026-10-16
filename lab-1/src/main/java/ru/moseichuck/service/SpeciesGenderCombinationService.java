package ru.moseichuck.service;

import ru.moseichuck.dto.CharacterDto;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeciesGenderCombinationService {

    private static final String COMBINATION_PATTERN = "%s - %s";

    public Set<String> collectCombinations(List<CharacterDto> characters) {
        Set<String> combinations = new HashSet<>();
        for (CharacterDto character : characters) {
            combinations.add(COMBINATION_PATTERN.formatted(character.getSpecies(), character.getGender()));
        }
        return combinations;
    }

    public void writeResult(Path file, Set<String> combinations) {
        List<String> sortedCombinations = combinations.stream().sorted().toList();
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("Уникальные комбинации (species, gender): " + sortedCombinations.size());
            writer.newLine();
            writer.newLine();
            for (String combination : sortedCombinations) {
                writer.write(combination);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать результат в файл " + file + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }
}
