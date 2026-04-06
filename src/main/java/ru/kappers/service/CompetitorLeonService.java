package ru.kappers.service;

import ru.kappers.model.leonmodels.CompetitorLeon;

import java.util.List;

/**
 * Интерфейс сервиса участника соревнования от букмекера Leon
 */
public interface CompetitorLeonService {

    CompetitorLeon save(CompetitorLeon comp);

    void delete(CompetitorLeon comp);

    CompetitorLeon update(CompetitorLeon comp);

    CompetitorLeon getById(long compId);

    List<CompetitorLeon> getAll();

    /**
     * Получить список участников соревнования по их id
     * @param ids идентификаторы команд
     * @return список участников соревнования
     */
    List<CompetitorLeon> getAllById(Iterable<Long> ids);

    /**
     * Получить список участников соревнования с id, не входящие в список указанных id
     * @param ids исключаемые id
     * @return список участников соревнования
     */
    List<CompetitorLeon> getAllByIdIsNotIn(Iterable<Long> ids);

    //todo Переписать на Optional чтобы не возвращать null
    CompetitorLeon getByName(String name);
}
