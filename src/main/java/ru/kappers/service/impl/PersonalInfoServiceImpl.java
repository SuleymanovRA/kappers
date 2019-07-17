package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kappers.model.PersonalInfo;
import ru.kappers.model.User;
import ru.kappers.repository.PersonalInfoRepository;
import ru.kappers.service.PersonalInfoService;

@Slf4j
@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    private final PersonalInfoRepository repository;

    @Autowired
    public PersonalInfoServiceImpl(PersonalInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public PersonalInfo addInfo(PersonalInfo info) {
        return repository.save(info);
    }

    @Override
    public void clearPersonalInfo(User user) {
        repository.deleteAllByUser(user);
    }

    @Override
    public PersonalInfo getByUser(User user) {
        return repository.getByUser(user);
    }

    @Override
    public PersonalInfo editInfo(PersonalInfo info) {
        //TODO
        return null;
    }
}
