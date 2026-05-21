package ru.kappers.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.model.catalog.Team;
import ru.kappers.repository.TeamRepository;
import ru.kappers.service.TeamService;

import java.util.List;

/**
 * Реализация сервиса команды
 */
@Slf4j
@Service
@Transactional
@Getter
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Team save(Team team) {
        log.debug("save(team: {})...", team);
        checkNotNull(team, "team is required not null");
        return teamRepository.save(team);
    }

    private <T> void checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    @Override
    public void delete(Team team) {
        log.debug("delete(team: {})...", team);
        checkNotNull(team, "team is required not null");
        teamRepository.delete(team);
    }

    @Transactional(readOnly = true)
    @Override
    public Team getById(int id) {
        log.debug("getById(id: {})...", id);
        return teamRepository.findById(id)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Team getByName(String name) {
        log.debug("getByName(name: {})...", name);
        return teamRepository.findFirstByName(name)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> getAllWithNameContains(String name) {
        log.debug("getAllWithNameContains(name: {})...", name);
        return teamRepository.findAllByNameContains(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> getAll() {
        log.debug("getAll()...");
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> getAllById(Iterable<Integer> ids) {
        log.debug("getAllById(ids: {})...", ids);
        return teamRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> getAllByIdIsNotIn(Iterable<Integer> ids) {
        log.debug("getAllByIdIsNotIn(ids: {})...", ids);
        return teamRepository.findAllByIdIsNotIn(ids);
    }
}
