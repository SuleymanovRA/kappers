package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kappers.model.Stat;
import ru.kappers.model.User;
import ru.kappers.model.utilmodel.IssueType;
import ru.kappers.repository.StatRepository;
import ru.kappers.service.StatService;

import java.util.List;
@Slf4j
@Service
public class StatServiceImpl implements StatService {

    private StatRepository repository;

    @Autowired
    public StatServiceImpl(StatRepository repository) {
        this.repository = repository;
    }

    @Override
    public Stat addRecord(Stat stat) {
        return repository.save(stat);
    }

    @Override
    public void deleteRecord(Stat stat) {
        repository.delete(stat);
    }

    @Override
    public void clearUserStat(User user) {
        repository.deleteAllByUser(user);
    }

    @Override
    public List<Stat> getUserStat(User user) {
        return repository.getAllByUser(user);
    }

    @Override
    public List<Stat> getAllByIssueType(IssueType type) {
        return repository.getAllByIssueType(type);
    }

    @Override
    public Stat updateStatRecord(Stat stat) {
        return null;
        //TODO
    }

    @Override
    public List<Stat> getAll() {
        return repository.findAll();
    }
}
