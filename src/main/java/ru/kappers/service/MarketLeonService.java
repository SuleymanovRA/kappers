package ru.kappers.service;

import ru.kappers.model.leonmodels.MarketLeon;

import java.util.List;

public interface MarketLeonService {
    //todo Переписать на Optional чтобы не возвращать null
    MarketLeon getByName(String name);
    MarketLeon getById (long id);
    List<MarketLeon> getAll();
    MarketLeon save(MarketLeon market);
}
