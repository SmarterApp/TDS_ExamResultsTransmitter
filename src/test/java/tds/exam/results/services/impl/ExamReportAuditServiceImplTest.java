/*******************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 *
 ******************************************************************************/

package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;

import tds.common.web.exceptions.NotFoundException;
import tds.exam.results.model.ExamReport;
import tds.exam.results.model.ExamReportStatus;
import tds.exam.results.repositories.ExamReportAuditRepository;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.trt.TDSReport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamReportAuditServiceImplTest {
    private ExamReportAuditService examReportAuditService;

    @Mock
    private ExamReportAuditRepository mockExamReportAuditRepository;

    private Marshaller marshaller;

    @Before
    public void setUp() throws JAXBException {
        marshaller = createMarshaller();
        examReportAuditService = new ExamReportAuditServiceImpl(mockExamReportAuditRepository, marshaller);
    }

    @Test
    public void shouldSaveExamReportSuccessful() throws JAXBException {
        final UUID examId = UUID.randomUUID();
        final TDSReport report = new TDSReport();

        ArgumentCaptor<String> trtCaptor = ArgumentCaptor.forClass(String.class);
        examReportAuditService.insertExamReport(examId, report, ExamReportStatus.RECEIVED);

        verify(mockExamReportAuditRepository).insertExamReport(eq(examId), trtCaptor.capture(), eq(ExamReportStatus.RECEIVED));

        StringWriter sw = new StringWriter();
        marshaller.marshal(report, sw);

        assertThat(trtCaptor.getValue()).isEqualTo(sw.toString());
    }

    @Test
    public void shouldUpdateExamStatusIfFound() {
        final UUID examId = UUID.randomUUID();
        ExamReport examReport = new ExamReport("xml", ExamReportStatus.RECEIVED, examId);
        when(mockExamReportAuditRepository.findLatestExamReport(examId)).thenReturn(Optional.of(examReport));

        examReportAuditService.updateExamReportStatus(examId, ExamReportStatus.PROCESSED);

        verify(mockExamReportAuditRepository).insertExamReport(examId, examReport.getReportXml(), ExamReportStatus.PROCESSED);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowIfStatusCannotBeFound() {
        final UUID examId = UUID.randomUUID();
        when(mockExamReportAuditRepository.findLatestExamReport(examId)).thenReturn(Optional.empty());

        examReportAuditService.updateExamReportStatus(examId, ExamReportStatus.PROCESSED);
    }

    private Marshaller createMarshaller() throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(TDSReport.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return contextObj.createMarshaller();
    }
}
