package tds.exam.results.mappers;

import org.joda.time.Instant;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import tds.exam.Exam;
import tds.exam.ExamineeAttribute;
import tds.exam.ExamineeContext;
import tds.exam.ExamineeRelationship;
import tds.exam.ExpandableExam;
import tds.exam.results.trt.Context;
import tds.exam.results.trt.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

public class ExamineeMapperTest {

    @Test
    public void shouldMapExamineeData() {
        List<ExamineeAttribute> mockExamineeAttributes = randomListOf(2, ExamineeAttribute.class);
        mockExamineeAttributes.add(
            new ExamineeAttribute.Builder()
                .withExamId(UUID.randomUUID())
                .withName("DOB")
                .withValue("10081990")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeAttributes.add(
            new ExamineeAttribute.Builder()
                .withExamId(UUID.randomUUID())
                .withName("Gender")
                .withValue("M")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeAttributes.add(
            new ExamineeAttribute.Builder()
                .withExamId(UUID.randomUUID())
                .withName("SSID")
                .withValue("1234567")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeAttributes.add(
            new ExamineeAttribute.Builder()
                .withExamId(UUID.randomUUID())
                .withName("LastName")
                .withValue("Danzig")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );

        ExpandableExam expandableExam = new ExpandableExam.Builder(random(Exam.class))
            .withExamineeAttributes(mockExamineeAttributes)
            .withExamineeRelationship(randomListOf(2, ExamineeRelationship.class))
            .build();

        TDSReport.Examinee examinee = ExamineeMapper.mapExaminee(expandableExam);

        assertThat(examinee.getExamineeAttributeOrExamineeRelationship().size())
            .isEqualTo(expandableExam.getExamineeRelationships().size() + expandableExam.getExamineeAttributes().size());

        ExamineeAttribute examineeAttribute = expandableExam.getExamineeAttributes().get(0);
        ExamineeRelationship examineeRelationship = expandableExam.getExamineeRelationships().get(0);

        TDSReport.Examinee.ExamineeAttribute attribute1 = null;
        TDSReport.Examinee.ExamineeRelationship relationship1 = null;

        for (Object relationshipOrAttribute : examinee.getExamineeAttributeOrExamineeRelationship()) {
            if (relationshipOrAttribute instanceof TDSReport.Examinee.ExamineeRelationship
                && ((TDSReport.Examinee.ExamineeRelationship) relationshipOrAttribute).getName().equals(examineeRelationship.getName())) {
                relationship1 = (TDSReport.Examinee.ExamineeRelationship) relationshipOrAttribute;
            } else if (relationshipOrAttribute instanceof TDSReport.Examinee.ExamineeAttribute
                && ((TDSReport.Examinee.ExamineeAttribute) relationshipOrAttribute).getName().equals(examineeAttribute.getName())) {
                attribute1 = (TDSReport.Examinee.ExamineeAttribute) relationshipOrAttribute;
                assertThat(attribute1.getValue()).isEqualTo(examineeAttribute.getValue());
                assertThat(attribute1.getContext()).isEqualTo(Context.fromValue(examineeAttribute.getContext().name()));
                assertThat(attribute1.getContextDate()).isNotNull();
            }
        }

        assertThat(attribute1.getValue()).isEqualTo(examineeAttribute.getValue());
        assertThat(attribute1.getContext()).isEqualTo(Context.fromValue(examineeAttribute.getContext().name()));
        assertThat(attribute1.getContextDate()).isNotNull();

        assertThat(relationship1.getValue()).isEqualTo(examineeRelationship.getValue());
        assertThat(relationship1.getContext()).isEqualTo(Context.fromValue(examineeRelationship.getContext().name()));
        assertThat(relationship1.getContextDate()).isNotNull();

        TDSReport.Examinee.ExamineeAttribute dateAttribute =
            (TDSReport.Examinee.ExamineeAttribute)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeAttribute
                    && ((TDSReport.Examinee.ExamineeAttribute)attrOrRel).getName().equalsIgnoreCase("Birthday"))
                .findFirst().get();
        assertThat(dateAttribute.getValue()).isEqualTo("1990-10-08");

        TDSReport.Examinee.ExamineeAttribute sexAttribute =
            (TDSReport.Examinee.ExamineeAttribute)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeAttribute
                    && ((TDSReport.Examinee.ExamineeAttribute)attrOrRel).getName().equalsIgnoreCase("Sex"))
                .findFirst().get();
        assertThat(sexAttribute.getValue()).isEqualTo("M");

        TDSReport.Examinee.ExamineeAttribute ssidAttribute =
            (TDSReport.Examinee.ExamineeAttribute)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeAttribute
                    && ((TDSReport.Examinee.ExamineeAttribute)attrOrRel).getName().equalsIgnoreCase("StudentIdentifier"))
                .findFirst().get();
        assertThat(ssidAttribute.getValue()).isEqualTo("1234567");

        TDSReport.Examinee.ExamineeAttribute lastNameAttribute =
            (TDSReport.Examinee.ExamineeAttribute)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeAttribute
                    && ((TDSReport.Examinee.ExamineeAttribute)attrOrRel).getName().equalsIgnoreCase("LastOrSurname"))
                .findFirst().get();
        assertThat(lastNameAttribute.getValue()).isEqualTo("Danzig");
    }
}
