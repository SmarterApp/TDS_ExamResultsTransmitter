package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

import tds.exam.results.repositories.TestIntegrationSystemRepository;
import tds.exam.results.services.TestIntegrationSystemService;
import tds.exam.results.trt.TDSReport;

@Service
public class TestIntegrationSystemServiceImpl implements TestIntegrationSystemService {
    private final Marshaller jaxbMarshaller;
    private final TestIntegrationSystemRepository testIntegrationSystemRepository;

    @Autowired
    public TestIntegrationSystemServiceImpl(final TestIntegrationSystemRepository testIntegrationSystemRepository,
                                            final Marshaller jaxbMarshaller) {
        this.testIntegrationSystemRepository = testIntegrationSystemRepository;
        this.jaxbMarshaller = jaxbMarshaller;
    }

    @Override
    public void sendResults(final UUID examId, final TDSReport report) throws JAXBException {
        final StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(report, sw);
        final String reportXml = sw.toString();
        testIntegrationSystemRepository.sendResults(examId, reportXml);
    }
}
