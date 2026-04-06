package ru.kappers.service;

import ru.kappers.model.leonmodels.LeagueLeon;

public interface LeagueLeonService {
    //todo Переписать на Optional чтобы не возвращался null
    LeagueLeon getByName(String name);
    LeagueLeon save(LeagueLeon league);
}
