package ru.moseichuck.dao;

import ru.moseichuck.dto.CharacterDto;
import ru.moseichuck.mapper.CharacterCsvMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharacterDaoCsv implements CharacterDao {

    private static final String HEADER = "id,name,status,species,type,gender,origin/name,location/name,created";

    private final Path csvFile;
    private final CharacterCsvMapper csvMapper;

    public CharacterDaoCsv(Path csvFile) {
        this.csvFile = csvFile;
        this.csvMapper = new CharacterCsvMapper();
    }

    @Override
    public List<CharacterDto> findAll() {
        List<CharacterDto> characters = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                characters.add(csvMapper.toDto(line));
            }
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл " + csvFile + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
        return characters;
    }

    @Override
    public Optional<CharacterDto> findById(long id) {
        return findAll().stream()
                .filter(character -> character.getId() == id)
                .findFirst();
    }

    @Override
    public CharacterDto create(CharacterDto character) {
        List<CharacterDto> characters = findAll();
        character.setId(nextId(characters));
        characters.add(character);
        writeAll(characters);
        return character;
    }

    @Override
    public void update(CharacterDto character) {
        List<CharacterDto> characters = findAll();
        for (int index = 0; index < characters.size(); index++) {
            if (characters.get(index).getId() == character.getId()) {
                characters.set(index, character);
                writeAll(characters);
                return;
            }
        }
        throw new IllegalArgumentException("Персонаж с идентификатором " + character.getId() + " не найден");
    }

    @Override
    public boolean deleteById(long id) {
        List<CharacterDto> characters = findAll();
        boolean removed = characters.removeIf(character -> character.getId() == id);
        if (removed) {
            writeAll(characters);
        }
        return removed;
    }

    private long nextId(List<CharacterDto> characters) {
        return characters.stream()
                .mapToLong(CharacterDto::getId)
                .max()
                .orElse(0L) + 1;
    }

    private void writeAll(List<CharacterDto> characters) {
        try (BufferedWriter writer = Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (CharacterDto character : characters) {
                writer.write(csvMapper.toCsvLine(character));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать файл " + csvFile + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }
}
