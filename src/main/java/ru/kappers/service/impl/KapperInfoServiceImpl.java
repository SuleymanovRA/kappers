package ru.kappers.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.exceptions.UserNotHaveKapperRoleException;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.Role;
import ru.kappers.model.User;
import ru.kappers.repository.KapperInfoRepository;
import ru.kappers.service.KapperInfoService;
import ru.kappers.service.MessageTranslator;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KapperInfoServiceImpl implements KapperInfoService {
    private final KapperInfoRepository kapperRepository;
    private final MessageTranslator translator;

    @Override
    public KapperInfo initKapper(User user) {
        log.debug("initKapper(user: {})...", user);
        if (user.hasNoRole(Role.Names.KAPPER)) {
            String message = translator.byCode("user.isNotKapperButTriedInitialize", user.getUserName());
            log.error(message);
            throw new UserNotHaveKapperRoleException(message);
        }
        KapperInfo kapperInfo = getByUser(user);
        if (kapperInfo != null) {
            log.error(translator.byCode("user.alreadyIsKapper", user.getUserName()));
            return kapperInfo;
        }
        kapperInfo = editKapper(defaultKapperInfoWithUser(user));
        user.setKapperInfo(kapperInfo);
        return kapperInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public KapperInfo getByUser(User user) {
        return kapperRepository.getKapperInfoByUser(user);
    }

    private KapperInfo defaultKapperInfoWithUser(User user) {
        return KapperInfo.builder()
                .user(user)
                .tokens(500)
                .bets(0)
                .blockedTokens(0)
                .failBets(0)
                .successBets(0)
                .build();
    }

    @Override
    public KapperInfo editKapper(KapperInfo kapperInfo) {
        return kapperRepository.save(kapperInfo);
    }

    @Override
    public void delete(User user) {
        kapperRepository.deleteByUser(user);
    }
}
