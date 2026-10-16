package ru.moseichuck;

import ru.moseichuck.dao.CharacterDao;
import ru.moseichuck.dao.CharacterDaoCsv;
import ru.moseichuck.dto.CharacterDto;
import ru.moseichuck.service.SpeciesGenderCombinationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class Main {

    private static final String SOURCE_RESOURCE = "/characters.csv";
    private static final Path WORKING_CSV = Path.of("target", "characters.csv");
    private static final Path RESULT_FILE = Path.of("target", "unique-species-gender.txt");

    public static void main(String[] args) {
        Path csvFile = prepareWorkingCsv();
        CharacterDao characterDao = new CharacterDaoCsv(csvFile);

        SpeciesGenderCombinationService service = new SpeciesGenderCombinationService();
        Set<String> combinations = service.collectCombinations(characterDao.findAll());
        service.writeResult(RESULT_FILE, combinations);

        System.out.println("Уникальных комбинаций (species, gender): " + combinations.size());
        System.out.println("Результат сохранён в " + RESULT_FILE.toAbsolutePath());

        demonstrateCrud(characterDao);
    }

    private static Path prepareWorkingCsv() {
        try (InputStream source = Main.class.getResourceAsStream(SOURCE_RESOURCE)) {
            if (source == null) {
                throw new IllegalStateException("Ресурс " + SOURCE_RESOURCE + " не найден");
            }
            Files.createDirectories(WORKING_CSV.getParent());
            Files.copy(source, WORKING_CSV, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Не удалось подготовить файл " + WORKING_CSV + ": " + e.getMessage());
            throw new UncheckedIOException(e);
        }
        return WORKING_CSV;
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

        created.setStatus("Dead");
        characterDao.update(created);
        System.out.println("Статус персонажа " + created.getId() + " обновлён на " + created.getStatus());

        boolean deleted = characterDao.deleteById(created.getId());
        System.out.println("Удалён персонаж " + created.getId() + ": " + deleted);

        System.out.println("Всего персонажей после CRUD: " + characterDao.findAll().size());
    }
}
