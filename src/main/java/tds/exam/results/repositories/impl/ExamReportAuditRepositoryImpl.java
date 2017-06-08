package tds.exam.results.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

import tds.exam.results.repositories.ExamReportAuditRepository;

import static tds.common.data.mapping.ResultSetMapperUtility.mapInstantToTimestamp;

@Repository
public class ExamReportAuditRepositoryImpl implements ExamReportAuditRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    ExamReportAuditRepositoryImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertExamReport(final UUID examId, final String examReportXml) {
        final SqlParameterSource parameters = new MapSqlParameterSource("examId", examId.toString())
            .addValue("examReportXml", examReportXml)
            .addValue("createdAt", mapInstantToTimestamp(Instant.now()));

        final String SQL =
            "INSERT INTO \n" +
                "   exam_report \n" +
                "( \n" +
                "   exam_id, \n" +
                "   report, \n" +
                "   created_at \n" +
                ") VALUES ( \n" +
                "   :examId, \n" +
                "   :examReportXml, \n" +
                "   :createdAt \n" +
                ")";

        jdbcTemplate.update(SQL, parameters);
    }
}