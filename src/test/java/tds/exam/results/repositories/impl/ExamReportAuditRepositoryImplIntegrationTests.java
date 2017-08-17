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

package tds.exam.results.repositories.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import tds.exam.results.model.ExamReport;
import tds.exam.results.model.ReportStatus;
import tds.exam.results.repositories.ExamReportAuditRepository;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class ExamReportAuditRepositoryImplIntegrationTests {
    private ExamReportAuditRepository examReportAuditRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        examReportAuditRepository = new ExamReportAuditRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void shouldInsertExamReport() {
        final UUID examId = UUID.randomUUID();
        final String report = "tds report";
        examReportAuditRepository.insertExamReport(examId, report, ReportStatus.RECEIVED);
        examReportAuditRepository.insertExamReport(examId, report, ReportStatus.SENT);

        Optional<ExamReport> maybeReport = examReportAuditRepository.findLatestExamReport(examId);

        ExamReport examReport = maybeReport.get();
        assertThat(examReport.getReportXml()).isEqualTo(report);
        assertThat(examReport.getStatus()).isEqualTo(ReportStatus.SENT);
        assertThat(examReport.getExamId()).isEqualTo(examId);
    }
}
