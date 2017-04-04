package tds.exam.results.mappers;

import java.util.List;

import tds.exam.ExpandableExam;
import tds.exam.results.mappers.utils.JaxbMapperUtils;
import tds.exam.results.trt.Context;
import tds.exam.results.trt.TDSReport;

/**
 * A mapper class for mapping examinee data to the JAXB {@link tds.exam.results.trt.TDSReport.Examinee} object
 */
public class ExamineeMapper {
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
            reportAttribute.setName(attribute.getName());
            reportAttribute.setValue(attribute.getValue());
            reportAttribute.setContext(Context.fromValue(attribute.getContext().name()));
            reportAttribute.setContextDate(JaxbMapperUtils.convertInstantToGregorianCalendar(attribute.getCreatedAt()));
            attributesAndRelationships.add(reportAttribute);
        });

        return examinee;
    }
}
