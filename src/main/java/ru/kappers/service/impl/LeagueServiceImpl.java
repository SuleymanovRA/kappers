package ru.kappers.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.model.catalog.League;
import ru.kappers.repository.LeagueRepository;
import ru.kappers.service.LeagueService;

import java.util.List;

/**
 * Реализация сервиса лиг
 */
@Slf4j
@Service
@Transactional
@Getter
public class LeagueServiceImpl implements LeagueService {
    private final LeagueRepository leagueRepository;

    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @Override
    public League save(League league) {
        log.debug("save(league: {})...", league);
        checkNotNull(league, "league is required not null");
        return leagueRepository.save(league);
    }

    private <T> void checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    @Override
    public void delete(League league) {
        log.debug("delete(league: {})...", league);
        checkNotNull(league, "league is required not null");
        leagueRepository.delete(league);
    }

    @Transactional(readOnly = true)
    @Override
    public League getById(int id) {
        log.debug("getById(id: {})...", id);
        return leagueRepository.findById(id)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public League getByName(String name) {
        log.debug("getByName(name: {})...", name);
        return leagueRepository.findFirstByName(name)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<League> getAllWithNameContains(String name) {
        log.debug("getAllWithNameContains(name: {})...", name);
        return leagueRepository.findAllByNameContains(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<League> getAll() {
        log.debug("getAll()...");
        return leagueRepository.findAll();
    }
}
