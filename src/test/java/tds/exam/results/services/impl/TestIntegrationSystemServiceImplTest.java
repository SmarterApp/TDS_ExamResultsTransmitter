package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.UUID;

import tds.exam.results.repositories.TestIntegrationSystemRepository;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.exam.results.trt.TDSReport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestIntegrationSystemServiceImplTest {
    private TestIntegrationSystemService testIntegrationSystemService;

    @Mock
    private TestIntegrationSystemRepository mockTestIntegrationSystemRepository;

    @Mock
    private Marshaller marshaller;

    @Before
    public void setUp() {
        testIntegrationSystemService = new TestIntegrationSystemServiceImpl(mockTestIntegrationSystemRepository, marshaller);
    }

    @Test
    public void shouldSendToTis() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        final TDSReport report = new TDSReport();

        testIntegrationSystemService.sendResults(examId, report);
        verify(mockTestIntegrationSystemRepository).sendResults(eq(examId), any());
    }
}
