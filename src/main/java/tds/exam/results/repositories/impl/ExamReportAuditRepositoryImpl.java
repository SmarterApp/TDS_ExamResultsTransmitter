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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import tds.exam.results.model.ExamReport;
import tds.exam.results.model.ReportStatus;
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
    public void insertExamReport(final UUID examId, final String examReportXml, final ReportStatus status) {
        final SqlParameterSource parameters = new MapSqlParameterSource("examId", examId.toString())
            .addValue("examReportXml", examReportXml)
            .addValue("createdAt", mapInstantToTimestamp(Instant.now()))
            .addValue("status", status.getValue());

        final String SQL =
            "INSERT INTO \n" +
                "   exam_report \n" +
                "( \n" +
                "   exam_id, \n" +
                "   report, \n" +
                "   status, \n" +
                "   created_at \n" +
                ") VALUES ( \n" +
                "   :examId, \n" +
                "   :examReportXml, \n" +
                "   :status, \n" +
                "   :createdAt \n" +
                ")";

        jdbcTemplate.update(SQL, parameters);
    }

    @Override
    public Optional<ExamReport> findLatestExamReport(final UUID examId) {
        final SqlParameterSource parameters = new MapSqlParameterSource("examId", examId.toString());

        final String SQL = "SELECT \n" +
            "  report, \n" +
            "  exam_id, \n" +
            "  status \n" +
            "FROM \n " +
            "  exam_report \n" +
            "WHERE \n" +
            "  exam_id = :examId \n" +
            "ORDER BY id DESC LIMIT 1";

        Optional<ExamReport> maybeExamReport;

        try {
            maybeExamReport = Optional.of(jdbcTemplate.queryForObject(SQL, parameters, new RowMapper<ExamReport>() {
                @Override
                public ExamReport mapRow(final ResultSet rs, final int i) throws SQLException {
                    return new ExamReport(
                        rs.getString("report"),
                        ReportStatus.fromValue(rs.getString("status")),
                        UUID.fromString(rs.getString("exam_id"))
                    );
                }
            }));
        } catch (EmptyResultDataAccessException e) {
            maybeExamReport = Optional.empty();
        }

        return maybeExamReport;
    }
}