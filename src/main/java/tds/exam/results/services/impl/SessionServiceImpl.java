package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import tds.common.cache.CacheType;
import tds.exam.results.repositories.SessionRepository;
import tds.exam.results.services.SessionService;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Session findSessionById(final UUID sessionId) {
        return sessionRepository.findSessionById(sessionId);
    }

    @Override
    public ExternalSessionConfiguration findExternalSessionConfigurationByClientName(final String clientName) {
        return sessionRepository.findExternalSessionConfigurationByClientName(clientName);
    }
}
