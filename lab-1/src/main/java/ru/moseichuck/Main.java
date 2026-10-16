package ru.moseichuck;

import ru.moseichuck.dao.CharacterDao;
import ru.moseichuck.dao.CharacterDaoCsv;
import ru.moseichuck.dto.CharacterDto;
import ru.moseichuck.mapper.SpeciesGenderCombinationMapper;
import ru.moseichuck.service.SpeciesGenderCombinationService;
import ru.moseichuck.writer.SpeciesGenderCombinationWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class Main {

    private static final String SOURCE_CSV_RESOURCE = "/characters.csv";
    private static final Path WORKING_COPY_CSV = Path.of("tmp", "characters-working-copy.csv");
    private static final Path RESULT_FILE = Path.of("tmp", "unique-species-gender.txt");

    public static void main(String[] args) {
        CharacterDao characterDao = new CharacterDaoCsv(resolveCsvFile());

        SpeciesGenderCombinationService service =
                new SpeciesGenderCombinationService(new SpeciesGenderCombinationMapper());
        Set<String> combinations = service.collectCombinations(characterDao.findAll());
        new SpeciesGenderCombinationWriter().write(RESULT_FILE, combinations);

        System.out.println("Уникальных комбинаций (species, gender): " + combinations.size());
        System.out.println("Результат сохранён в " + RESULT_FILE.toAbsolutePath());

        demonstrateCrud(characterDao);
    }

    private static Path resolveCsvFile() {
        URL resource = Main.class.getResource(SOURCE_CSV_RESOURCE);
        if (resource == null) {
            throw new IllegalStateException("Ресурс " + SOURCE_CSV_RESOURCE + " не найден");
        }
        if ("file".equals(resource.getProtocol())) {
            try {
                return Path.of(resource.toURI());
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Некорректный путь к ресурсу " + SOURCE_CSV_RESOURCE, e);
            }
        }
        return prepareWorkingCopy(resource);
    }

    private static Path prepareWorkingCopy(URL resource) {
        try (InputStream source = resource.openStream()) {
            Files.createDirectories(WORKING_COPY_CSV.toAbsolutePath().getParent());
            Files.copy(source, WORKING_COPY_CSV, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Не удалось подготовить рабочую копию " + WORKING_COPY_CSV + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
        return WORKING_COPY_CSV;
    }

    private static void demonstrateCrud(CharacterDao characterDao) {
        System.out.println();
        System.out.println("Демонстрация CRUD:");

        CharacterDto created = characterDao.create(new CharacterDto(
                0L,
                "Rick Prime",
                "Alive",
                "Human",
                "",
                "Male",
                "Earth (C-137)",
                "Citadel of Ricks",
                "2024-01-01T00:00:00.000Z"
        ));
        System.out.println("Создан персонаж с идентификатором " + created.getId() + ": " + created.getName());

        CharacterDto twentiethCharacter = characterDao.findAll().get(19);
        boolean deleted = characterDao.deleteById(twentiethCharacter.getId());
        System.out.println("Удалён 20-й персонаж " + twentiethCharacter.getId() + ": "
                + twentiethCharacter.getName() + " (" + deleted + ")");

        System.out.println("Всего персонажей после CRUD: " + characterDao.findAll().size());
    }
}
