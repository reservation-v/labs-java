package ru.moseichuck.dao;

import ru.moseichuck.dto.CharacterDto;

import java.util.List;
import java.util.Optional;

public interface CharacterDao {

    List<CharacterDto> findAll();

    Optional<CharacterDto> findById(long id);

    CharacterDto create(CharacterDto character);

    void update(CharacterDto character);

    boolean deleteById(long id);
}
