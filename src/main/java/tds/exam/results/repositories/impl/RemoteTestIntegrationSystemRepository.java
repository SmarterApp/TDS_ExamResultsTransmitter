package tds.exam.results.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.TestIntegrationSystemRepository;

@Repository
public class RemoteTestIntegrationSystemRepository implements TestIntegrationSystemRepository {
    private final RestTemplate restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteTestIntegrationSystemRepository(final RestTemplate restTemplate,
                                                 final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void sendResults(final UUID examId, final String results) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<?> requestHttpEntity = new HttpEntity<>(results, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/api/testresult", properties.getTisUrl()));

        restTemplate.exchange(
            builder.build().toUri(),
            HttpMethod.POST,
            requestHttpEntity,
            String.class);
    }
}
