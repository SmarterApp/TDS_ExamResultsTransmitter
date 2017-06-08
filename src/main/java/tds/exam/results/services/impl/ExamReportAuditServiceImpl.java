package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tds.exam.results.repositories.ExamReportAuditRepository;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.trt.TDSReport;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

@Service
public class ExamReportAuditServiceImpl implements ExamReportAuditService {
    private final ExamReportAuditRepository examReportAuditRepository;
    private final Marshaller jaxbMarshaller;

    @Autowired
    public ExamReportAuditServiceImpl(final ExamReportAuditRepository examReportAuditRepository,
                                      final Marshaller jaxbMarshaller) {
        this.examReportAuditRepository = examReportAuditRepository;
        this.jaxbMarshaller = jaxbMarshaller;
    }

    @Override
    public void insertExamReport(final UUID examId, final TDSReport report) {
        final StringWriter sw = new StringWriter();

        try {
            jaxbMarshaller.marshal(report, sw);
            final String reportXml = sw.toString();
            examReportAuditRepository.insertExamReport(examId, reportXml);
        } catch (final JAXBException e) {
            throw new RuntimeException("Failed to marshall TDSReport into XML", e);
        }

    }
}
