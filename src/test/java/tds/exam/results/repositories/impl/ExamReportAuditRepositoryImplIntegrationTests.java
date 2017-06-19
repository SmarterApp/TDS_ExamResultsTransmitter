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

package tds.exam.results.repositories.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        examReportAuditRepository.insertExamReport(examId, report);

        final String reportXml = jdbcTemplate.queryForObject("SELECT report FROM exam_report WHERE exam_id = :examId",
            new MapSqlParameterSource("examId", examId.toString()), String.class);

        assertThat(reportXml).isEqualTo(report);
    }
}
