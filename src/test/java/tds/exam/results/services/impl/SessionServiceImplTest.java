package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import tds.exam.results.repositories.SessionRepository;
import tds.exam.results.services.SessionService;
import tds.session.Session;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceImplTest {
    private SessionService sessionService;

    @Mock
    private SessionRepository mockSessionRepository;

    @Before
    public void setup() {
        sessionService = new SessionServiceImpl(mockSessionRepository);
    }

    @Test
    public void shouldFindSession() {
        UUID sessionId = UUID.randomUUID();
        Session session = random(Session.class);

        when(mockSessionRepository.findSessionById(sessionId)).thenReturn(session);
        Session retSession = sessionService.findSessionById(sessionId);
        verify(mockSessionRepository).findSessionById(sessionId);

        assertThat(retSession).isEqualTo(session);
    }
}
