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
