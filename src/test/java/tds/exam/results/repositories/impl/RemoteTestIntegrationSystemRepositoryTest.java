package tds.exam.results.repositories.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.TestIntegrationSystemRepository;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteTestIntegrationSystemRepositoryTest {
    private TestIntegrationSystemRepository testIntegrationSystemRepository;

    @Mock
    private OAuth2RestOperations mockRestTemplate;

    @Mock
    private ExamResultsTransmitterServiceProperties mockProperties;

    @Before
    public void setUp() {
        testIntegrationSystemRepository = new RemoteTestIntegrationSystemRepository(mockRestTemplate, mockProperties);
    }

    @Test
    public void shouldSendResultsToTis() {
        final UUID examId = UUID.randomUUID();
        final String results = "TestResults";

        when(mockProperties.getTisUrl()).thenReturn("http://localhost:1234");
        when(mockProperties.isSendToTis()).thenReturn(true);
        testIntegrationSystemRepository.sendResults(examId, results);

        verify(mockRestTemplate).exchange(isA(URI.class), isA(HttpMethod.class), isA(HttpEntity.class), isA(Class.class));
    }

    @Test
    public void shouldNotSendResultsToTis() {
        final UUID examId = UUID.randomUUID();
        final String results = "TestResults";

        when(mockProperties.getTisUrl()).thenReturn("http://localhost:1234");
        when(mockProperties.isSendToTis()).thenReturn(false);
        testIntegrationSystemRepository.sendResults(examId, results);

        verify(mockRestTemplate, never()).exchange(isA(URI.class), isA(HttpMethod.class), isA(HttpEntity.class), isA(Class.class));
    }
}
