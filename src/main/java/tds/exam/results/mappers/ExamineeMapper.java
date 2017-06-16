package tds.exam.results.mappers;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import tds.exam.ExpandableExam;
import tds.exam.results.mappers.utils.JaxbMapperUtils;
import tds.exam.results.trt.Context;
import tds.exam.results.trt.TDSReport;

/**
 * A mapper class for mapping examinee data to the JAXB {@link tds.exam.results.trt.TDSReport.Examinee} object
 */
public class ExamineeMapper {
    private static final String DEFAULT_BIRTH_DATE = "1900-01-01";
    private static final String DOB_ATTRIBUTE_ID = "DOB";
    private static final Logger log = LoggerFactory.getLogger(ExamineeMapper.class);

    private static final ImmutableMap<String, String> attributeNames = ImmutableMap.of(
        DOB_ATTRIBUTE_ID, "Birthday",
        "Gender", "Sex",
        "LastName", "LastOrSurname",
        "SSID", "StudentIdentifier"
    );

    public static TDSReport.Examinee mapExaminee(final ExpandableExam expandableExam) {
        TDSReport.Examinee examinee = new TDSReport.Examinee();
        examinee.setKey(expandableExam.getExam().getStudentId());
        List<Object> attributesAndRelationships = examinee.getExamineeAttributeOrExamineeRelationship();

        expandableExam.getExamineeRelationships().forEach(relationship -> {
            TDSReport.Examinee.ExamineeRelationship reportRelationship = new TDSReport.Examinee.ExamineeRelationship();
            reportRelationship.setName(relationship.getName());
            reportRelationship.setValue(relationship.getValue());
            reportRelationship.setContext(Context.fromValue(relationship.getContext().name()));
            reportRelationship.setContextDate(JaxbMapperUtils.convertInstantToGregorianCalendar(relationship.getCreatedAt()));
            attributesAndRelationships.add(reportRelationship);
        });

        expandableExam.getExamineeAttributes().forEach(attribute -> {
            TDSReport.Examinee.ExamineeAttribute reportAttribute = new TDSReport.Examinee.ExamineeAttribute();
            // If there is a translated attribute name, use the translated name - See ReportingDLL - 1920-1927
            reportAttribute.setName(
                attributeNames.containsKey(attribute.getName())
                    ? attributeNames.get(attribute.getName())
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
