package ru.moseichuck.dao;

import ru.moseichuck.dto.CharacterDto;

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
    private static final int COLUMN_COUNT = 9;
    private static final int FIRST_COLUMN = 0;

    private final Path csvFile;

    public CharacterDaoCsv(Path csvFile) {
        this.csvFile = csvFile;
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
                characters.add(toCharacter(splitCsvLine(line)));
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
                writer.write(toCsvLine(character));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать файл " + csvFile + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    private CharacterDto toCharacter(List<String> columns) {
        if (columns.size() != COLUMN_COUNT) {
            throw new IllegalArgumentException(
                    "Ожидалось " + COLUMN_COUNT + " колонок, получено " + columns.size());
        }
        return new CharacterDto(
                Long.parseLong(columns.get(FIRST_COLUMN).trim()),
                columns.get(1),
                columns.get(2),
                columns.get(3),
                columns.get(4),
                columns.get(5),
                columns.get(6),
                columns.get(7),
                columns.get(8)
        );
    }

    private String toCsvLine(CharacterDto character) {
        return String.join(",",
                String.valueOf(character.getId()),
                escape(character.getName()),
                escape(character.getStatus()),
                escape(character.getSpecies()),
                escape(character.getType()),
                escape(character.getGender()),
                escape(character.getOriginName()),
                escape(character.getLocationName()),
                escape(character.getCreatedAt())
        );
    }

    private List<String> splitCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder column = new StringBuilder();
        boolean insideQuotes = false;
        for (int index = 0; index < line.length(); index++) {
            char symbol = line.charAt(index);
            if (insideQuotes) {
                if (symbol == '"') {
                    if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                        column.append('"');
                        index++;
                    } else {
                        insideQuotes = false;
                    }
                } else {
                    column.append(symbol);
                }
            } else if (symbol == '"') {
                insideQuotes = true;
            } else if (symbol == ',') {
                columns.add(column.toString());
                column.setLength(0);
            } else {
                column.append(symbol);
            }
        }
        columns.add(column.toString());
        return columns;
    }

    private String escape(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
