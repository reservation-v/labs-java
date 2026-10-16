package ru.moseichuck.mapper;

import ru.moseichuck.dto.CharacterDto;

import java.util.ArrayList;
import java.util.List;

public class CharacterCsvMapper {

    private static final int COLUMN_COUNT = 9;
    private static final int FIRST_COLUMN = 0;

    public CharacterDto toDto(String csvLine) {
        List<String> columns = splitCsvLine(csvLine);
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

    public String toCsvLine(CharacterDto character) {
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
