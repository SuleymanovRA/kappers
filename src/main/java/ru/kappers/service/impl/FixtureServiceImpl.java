package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.model.Fixture;
import ru.kappers.model.Fixture.Status;
import ru.kappers.repository.FixtureRepository;
import ru.kappers.service.FixtureService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Cервис спортивных событий
 */
@Slf4j
@Service
@Transactional
public class FixtureServiceImpl implements FixtureService {

    private final FixtureRepository repository;

    @Autowired
    public FixtureServiceImpl(FixtureRepository repository) {
        this.repository = repository;
    }

    @Override
    public Fixture addRecord(Fixture fixture) {
        log.debug("addRecord(fixture: {})...", fixture);
        return repository.save(fixture);
    }

    @Override
    public void deleteRecord(Fixture fixture) {
        log.debug("deleteRecord(fixture: {})...", fixture);
        repository.delete(fixture);
    }

    @Override
    public void deleteRecordById(int id) {
        log.debug("deleteRecordById(id: {})...", id);
        deleteRecord(getById(id));
    }

    @Override
    public Fixture updateFixture(Fixture fixture) {
        log.debug("updateFixture(fixture: {})...", fixture);
        return repository.save(fixture);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getAll() {
        log.debug("getAll()...");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getAll(Pageable pageable) {
        log.debug("getAll(pageable: {})...", pageable);
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesByPeriod(Timestamp from, Timestamp to) {
        log.debug("getFixturesByPeriod(from: {}, to: {})...", from, to);
        return repository.getFixturesByPeriod(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesByPeriod(Timestamp from, Timestamp to, Pageable pageable) {
        log.debug("getFixturesByPeriod(from: {}, to: {}, pageable: {})...", from, to, pageable);
        return repository.getFixturesByPeriod(from, to, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesByPeriod(LocalDateTime from, LocalDateTime to) {
        log.debug("getFixturesByPeriod(from: {}, to: {})...", from, to);
        return getFixturesByPeriod(Timestamp.valueOf(from), Timestamp.valueOf(to));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesByPeriod(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        log.debug("getFixturesByPeriod(from: {}, to: {}, pageable: {})...", from, to, pageable);
        return getFixturesByPeriod(Timestamp.valueOf(from), Timestamp.valueOf(to), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesByPeriod(LocalDateTime from, LocalDateTime to, Status filter) {
        log.debug("getFixturesByPeriod(from: {}, to: {}, filter: {})...", from, to, filter);
        return repository.getFixturesByPeriod(Timestamp.valueOf(from), Timestamp.valueOf(to), filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesByPeriod(LocalDateTime from, LocalDateTime to, Status filter, Pageable pageable) {
        log.debug("getFixturesByPeriod(from: {}, to: {}, filter: {}, pageable: {})...", from, to, filter, pageable);
        return repository.getFixturesByPeriod(Timestamp.valueOf(from), Timestamp.valueOf(to), filter, pageable);
    }

    protected LocalDateTime getFromNow(LocalDate now) {
        return LocalDateTime.of(now, LocalTime.MIN);
    }

    protected LocalDateTime getToNow(LocalDate now) {
        return LocalDateTime.of(now, LocalTime.MAX);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesToday() {
        log.debug("getFixturesToday()...");
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesToday(Pageable pageable) {
        log.debug("getFixturesToday(pageable)...");
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesToday(Status filter) {
        log.debug("getFixturesToday(filter: {})...", filter);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now), filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesToday(Status filter, Pageable pageable) {
        log.debug("getFixturesToday(filter: {}, pageable: {})...", filter, pageable);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now), filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesLastWeek() {
        log.debug("getFixturesLastWeek()...");
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now).minusDays(7), getToNow(now));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesLastWeek(Pageable pageable) {
        log.debug("getFixturesLastWeek(pageable: {})...", pageable);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now).minusDays(7), getToNow(now), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesLastWeek(Status filter) {
        log.debug("getFixturesLastWeek(filter: {})...", filter);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now).minusDays(7), getToNow(now), filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesLastWeek(Status filter, Pageable pageable) {
        log.debug("getFixturesLastWeek(filter: {}, pageable: {})...", filter, pageable);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now).minusDays(7), getToNow(now), filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesNextWeek() {
        log.debug("getFixturesNextWeek()...");
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now).plusDays(7));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesNextWeek(Pageable pageable) {
        log.debug("getFixturesNextWeek(pageable: {})...", pageable);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now).plusDays(7), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fixture> getFixturesNextWeek(Status filter) {
        log.debug("getFixturesNextWeek(filter: {})...", filter);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now).plusDays(7), filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Fixture> getFixturesNextWeek(Status filter, Pageable pageable) {
        log.debug("getFixturesNextWeek(filter: {}, pageable: {})...", filter, pageable);
        final LocalDate now = LocalDate.now();
        return getFixturesByPeriod(getFromNow(now), getToNow(now).plusDays(7), filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Fixture getById(int id) {
        log.debug("getById(id: {})...", id);
        return repository.findById(id).orElse(null);
    }
}
