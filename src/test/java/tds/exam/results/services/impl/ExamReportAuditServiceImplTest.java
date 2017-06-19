/*******************************************************************************
 * Copyright 2016 Smarter Balance Licensed under the
 *     Educational Community License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may
 *     obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an "AS IS"
 *     BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing
 *     permissions and limitations under the License.
 ******************************************************************************/

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
    public void shouldSaveExamReportSuccessful() {
        final UUID examId = UUID.randomUUID();
        final TDSReport report = new TDSReport();
        examReportAuditService.insertExamReport(examId, report);
        verify(mockExamReportAuditRepository).insertExamReport(eq(examId), any());
    }
}
