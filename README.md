# Лабораторная работа №1 (вариант 9)

Вариант: `HashSet<String>` - уникальные комбинации `species - gender`.

## Что делает программа

1. Читает персонажей Рика и Морти из CSV в ресурсах (`lab-1/src/main/resources/characters.csv`).
2. Собирает уникальные комбинации вида `species - gender` в `HashSet<String>`.
3. Пишет результат в файл `tmp/unique-species-gender.txt` (комбинации отсортированы для стабильного вывода).
4. Демонстрирует CRUD: добавляет нового персонажа и заменяет им 20-го персонажа файла.

## Структура кода

- `Main` - точка входа: подготовка файла с данными, вызов сервиса, демонстрация CRUD.
- `dao` - доступ к данным: интерфейс `CharacterDao` и реализация `CharacterDaoCsv`.
- `mapper` - маппинг: `CharacterCsvMapper` (строка csv <-> dto) и `SpeciesGenderCombinationMapper` (dto -> комбинация).
- `service` - обработка: `SpeciesGenderCombinationService` собирает уникальные комбинации в `HashSet`.
- `writer` - запись результата в файл: `SpeciesGenderCombinationWriter`.
- `dto` - `CharacterDto`.

## Сборка и запуск

```bash
mvn clean package
cd lab-1
java -jar target/lab-1-1.0.0-SNAPSHOT-fat.jar
```

Запуск производится в директории `lab-1`, потому что файлы, создаваемые программой, попадают в каталог `tmp` рабочей директории.

Исходный `characters.csv` лежит в ресурсах.

## Результат

Файл `tmp/unique-species-gender.txt` с уникальными комбинациями видов и полов.
