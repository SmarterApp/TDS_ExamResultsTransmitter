package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

import tds.exam.results.repositories.ExamReportAuditRepository;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.trt.TDSReport;

@Service
public class ExamReportAuditServiceImpl implements ExamReportAuditService {
    private final ExamReportAuditRepository examReportAuditRepository;
    private final Marshaller jaxbMarshaller;

    @Autowired
    public ExamReportAuditServiceImpl(final ExamReportAuditRepository examReportAuditRepository,
                                      final JAXBContext jaxbContext) throws JAXBException {
        this.examReportAuditRepository = examReportAuditRepository;
        jaxbMarshaller = jaxbContext.createMarshaller();
    }

    @Override
    public void insertExamReport(final UUID examId, final TDSReport report) throws JAXBException {
        final StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(report, sw);
        final String reportXml = sw.toString();
        examReportAuditRepository.insertExamReport(examId, reportXml);
    }
}
