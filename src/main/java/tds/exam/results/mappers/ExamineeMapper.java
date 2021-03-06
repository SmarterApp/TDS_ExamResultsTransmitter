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

package tds.exam.results.mappers;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import tds.exam.ExpandableExam;
import tds.exam.results.mappers.utils.JaxbMapperUtils;
import tds.trt.model.Context;
import tds.trt.model.TDSReport;

/**
 * A mapper class for mapping examinee data to the JAXB {@link tds.exam.results.trt.TDSReport.Examinee} object
 */
public class ExamineeMapper {
    private static final String DEFAULT_BIRTH_DATE = "1900-01-01";
    private static final String DOB_ATTRIBUTE_ID = "DOB";
    private static final Logger log = LoggerFactory.getLogger(ExamineeMapper.class);

    private static final Map<String, String> trtAttributeRelationshipTranslations = ImmutableMap.<String, String>builder()
        .put(DOB_ATTRIBUTE_ID, "Birthdate")
        .put("Gender", "Sex")
        .put("LastName", "LastOrSurname")
        .put("SSID", "StudentIdentifier")
        .put("DistrictID", "ResponsibleDistrictIdentifier")
        .put("DistrictName", "OrganizationName")
        .put("SchoolID", "ResponsibleInstitutionIdentifier")
        .put("SchoolName", "NameOfInstitution")
        .build();

    public static TDSReport.Examinee mapExaminee(final ExpandableExam expandableExam) {
        TDSReport.Examinee examinee = new TDSReport.Examinee();
        examinee.setKey(expandableExam.getExam().getStudentId());
        List<Object> attributesAndRelationships = examinee.getExamineeAttributeOrExamineeRelationship();

        expandableExam.getExamineeRelationships().forEach(relationship -> {
            TDSReport.Examinee.ExamineeRelationship reportRelationship = new TDSReport.Examinee.ExamineeRelationship();
            // If there is a translated relationship name, use the translated name - See ReportingDLL - 1967-1972
            reportRelationship.setName(
                trtAttributeRelationshipTranslations.containsKey(relationship.getName())
                    ? trtAttributeRelationshipTranslations.get(relationship.getName())
                    : relationship.getName()
            );
            reportRelationship.setValue(relationship.getValue());
            reportRelationship.setContext(Context.fromValue(relationship.getContext().name()));
            reportRelationship.setContextDate(JaxbMapperUtils.convertInstantToGregorianCalendar(relationship.getCreatedAt()));
            attributesAndRelationships.add(reportRelationship);
        });

        expandableExam.getExamineeAttributes().forEach(attribute -> {
            TDSReport.Examinee.ExamineeAttribute reportAttribute = new TDSReport.Examinee.ExamineeAttribute();
            // If there is a translated attribute name, use the translated name - See ReportingDLL - 1920-1927
            reportAttribute.setName(
                trtAttributeRelationshipTranslations.containsKey(attribute.getName())
                    ? trtAttributeRelationshipTranslations.get(attribute.getName())
                    : attribute.getName()
            );
            reportAttribute.setValue(
                attribute.getName().equalsIgnoreCase(DOB_ATTRIBUTE_ID)
                    ? formatBirthDate(attribute.getValue())
                    : attribute.getValue()
            );
            reportAttribute.setContext(Context.fromValue(attribute.getContext().name()));
            reportAttribute.setContextDate(JaxbMapperUtils.convertInstantToGregorianCalendar(attribute.getCreatedAt()));
            attributesAndRelationships.add(reportAttribute);
        });

        return examinee;
    }

    /*
        The birthdate is typically formatted "MMDDYYYY" and is expected to be in "YYYY-MM-DD" - ReportingDLL.readTesteeAttributes()
     */
    private static String formatBirthDate(final String dob) {
        if (StringUtils.isBlank(dob)) {
            return DEFAULT_BIRTH_DATE;
        }

        DateFormat tdsFormat = new SimpleDateFormat("MMddyyyy");
        DateFormat trtFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return trtFormat.format(tdsFormat.parse(dob));
        } catch (ParseException e) {
            log.warn("Unable to parse birth date value, cannot translate to expected TRT date format: {}", dob);
            // See line 2391 in ReportingDLL.java
            return DEFAULT_BIRTH_DATE;
        }
    }
}
