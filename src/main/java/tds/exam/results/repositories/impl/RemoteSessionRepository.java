package tds.exam.results.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.SessionRepository;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

import static tds.exam.results.configuration.SupportApplicationConfiguration.SESSION_APP_CONTEXT;

@Repository
public class RemoteSessionRepository implements SessionRepository {
    private final RestTemplate restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteSessionRepository(final RestTemplate restTemplate, final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Session findSessionById(final UUID sessionId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/%s",
            properties.getSessionUrl(),
            SESSION_APP_CONTEXT,
            sessionId));

        return restTemplate.getForObject(builder.build().toUri(), Session.class);
    }

    @Override
    public ExternalSessionConfiguration findExternalSessionConfigurationByClientName(final String clientName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/external-config/%s",
            properties.getSessionUrl(),
            SESSION_APP_CONTEXT,
            clientName));

        return restTemplate.getForObject(builder.build().toUri(), ExternalSessionConfiguration.class);
    }
}
