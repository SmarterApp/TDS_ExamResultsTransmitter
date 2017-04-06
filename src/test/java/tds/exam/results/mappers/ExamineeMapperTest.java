package tds.exam.results.mappers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import tds.exam.Exam;
import tds.exam.ExamineeAttribute;
import tds.exam.ExamineeNote;
import tds.exam.ExamineeRelationship;
import tds.exam.ExpandableExam;
import tds.exam.results.trt.Context;
import tds.exam.results.trt.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExamineeMapperTest {

    @Test
    public void shouldMapExamineeData() {
        ExpandableExam expandableExam = new ExpandableExam.Builder(random(Exam.class))
            .withExamineeAttributes(randomListOf(2, ExamineeAttribute.class))
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
            }
        }

        assertThat(attribute1.getValue()).isEqualTo(examineeAttribute.getValue());
        assertThat(attribute1.getContext()).isEqualTo(Context.fromValue(examineeAttribute.getContext().name()));
        assertThat(attribute1.getContextDate()).isNotNull();

        assertThat(relationship1.getValue()).isEqualTo(examineeRelationship.getValue());
        assertThat(relationship1.getContext()).isEqualTo(Context.fromValue(examineeRelationship.getContext().name()));
        assertThat(relationship1.getContextDate()).isNotNull();
    }
}
