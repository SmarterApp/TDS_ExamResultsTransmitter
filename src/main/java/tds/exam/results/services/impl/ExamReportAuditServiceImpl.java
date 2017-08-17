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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.UUID;

import tds.exam.results.model.ReportStatus;
import tds.exam.results.repositories.ExamReportAuditRepository;
import tds.exam.results.services.ExamReportAuditService;
import tds.exam.results.trt.TDSReport;

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
    public void insertExamReport(final UUID examId, final TDSReport report, final ReportStatus status) {
        final StringWriter sw = new StringWriter();

        try {
            jaxbMarshaller.marshal(report, sw);
            examReportAuditRepository.insertExamReport(examId, sw.toString(), status);
        } catch (final JAXBException e) {
            throw new RuntimeException("Failed to marshall TDSReport into XML", e);
        }
    }
}
