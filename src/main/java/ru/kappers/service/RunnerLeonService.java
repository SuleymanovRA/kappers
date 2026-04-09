package ru.kappers.service;

import ru.kappers.model.leonmodels.MarketLeon;
import ru.kappers.model.leonmodels.OddsLeon;
import ru.kappers.model.leonmodels.RunnerLeon;

import java.util.List;

public interface RunnerLeonService {
    List<RunnerLeon> getByMarketAndOdd(long marketId, long oddId);
    List<RunnerLeon> getByOdd(long oddId);
    List<RunnerLeon> getAll();
    RunnerLeon save(RunnerLeon runner);
    void delete (RunnerLeon runner);
    RunnerLeon getById(long id);
    void deleteAllByOddId(long oddId);
    List<RunnerLeon> saveAll(List<RunnerLeon> runners);
    //todo Переписать на Optional чтобы не возвращать null
    RunnerLeon getFirstByMarketAndOddAndName(long marketId, long oddId, String name);
}
