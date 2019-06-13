package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kappers.model.History;
import ru.kappers.model.User;
import ru.kappers.repository.HistoryRepository;
import ru.kappers.service.HistoryService;

import java.util.List;

@Slf4j
@Service
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository repository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public History addHistoryRecord(History record) {
        return repository.save(record);
    }

    @Override
    public void delete(History record) {
        repository.delete(record);
    }

    @Override
    public void clearHistory(User user) {
        repository.deleteAllByUser(user);
    }

    @Override
    public History getById(int id) {
        return repository.getById(id);
    }

    @Override
    public List<History> getUsersHistory(User user) {
        return repository.getByUser(user);
    }

    @Override
    public History editHistory(History record) {
        return repository.save(record);
    }

    @Override
    public List<History> getAll() {
        return repository.findAll();
    }
}
