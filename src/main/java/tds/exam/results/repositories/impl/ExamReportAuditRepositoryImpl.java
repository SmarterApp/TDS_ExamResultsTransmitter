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