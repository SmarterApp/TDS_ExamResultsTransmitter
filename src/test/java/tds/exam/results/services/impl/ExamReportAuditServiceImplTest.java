package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.UUID;

import tds.exam.results.repositories.ExamReportAuditRepository;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.trt.TDSReport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExamReportAuditServiceImplTest {
    private ExamReportAuditService examReportAuditService;

    @Mock
    private ExamReportAuditRepository mockExamReportAuditRepository;

    @Mock
    private Marshaller mockMarshaller;

    @Before
    public void setUp() throws JAXBException {
        examReportAuditService = new ExamReportAuditServiceImpl(mockExamReportAuditRepository, mockMarshaller);
    }

    @Test
    public void shouldSaveExamReportSuccessful() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        final TDSReport report = new TDSReport();
        examReportAuditService.insertExamReport(examId, report);
        verify(mockExamReportAuditRepository).insertExamReport(eq(examId), any());
    }
}
