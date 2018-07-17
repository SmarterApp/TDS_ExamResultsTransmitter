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

import org.joda.time.Instant;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import tds.exam.Exam;
import tds.exam.ExamineeAttribute;
import tds.exam.ExamineeContext;
import tds.exam.ExamineeRelationship;
import tds.exam.ExpandableExam;
import tds.exam.results.trt.Context;
import tds.trt.model.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

public class ExamineeMapperTest {

    @Test
    public void shouldMapExamineeDataNullDOB() {
        List<ExamineeRelationship> mockExamineeRelationships = getExamineeRelationships();
        List<ExamineeAttribute> mockExamineeAttributes = Arrays.asList(
            new ExamineeAttribute.Builder()
                .withExamId(UUID.randomUUID())
                .withName("DOB")
                .withValue(null)
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        ExpandableExam expandableExam = new ExpandableExam.Builder(random(Exam.class))
            .withExamineeAttributes(mockExamineeAttributes)
            .withExamineeRelationship(mockExamineeRelationships)
            .build();
        
        TDSReport.Examinee examinee = ExamineeMapper.mapExaminee(expandableExam);

        assertThat(examinee.getExamineeAttributeOrExamineeRelationship().size())
            .isEqualTo(expandableExam.getExamineeRelationships().size() + expandableExam.getExamineeAttributes().size());

        ExamineeAttribute examineeAttribute = expandableExam.getExamineeAttributes().get(0);

        TDSReport.Examinee.ExamineeAttribute dobAttribute;

        for (Object relationshipOrAttribute : examinee.getExamineeAttributeOrExamineeRelationship()) {
            if (relationshipOrAttribute instanceof TDSReport.Examinee.ExamineeAttribute
                && ((TDSReport.Examinee.ExamineeAttribute) relationshipOrAttribute).getName().equals("Birthdate")){
                dobAttribute = (TDSReport.Examinee.ExamineeAttribute) relationshipOrAttribute;
                assertThat(dobAttribute.getValue()).isEqualTo("1900-01-01");
                assertThat(dobAttribute.getContext().value()).isEqualTo(examineeAttribute.getContext().name());
                assertThat(dobAttribute.getContextDate()).isNotNull();
            }
        }
    }

    @Test
    public void shouldMapExamineeData() {
        List<ExamineeRelationship> mockExamineeRelationships = getExamineeRelationships();
        List<ExamineeAttribute> mockExamineeAttributes = getExamineeAttributes();
        ExpandableExam expandableExam = new ExpandableExam.Builder(random(Exam.class))
            .withExamineeAttributes(mockExamineeAttributes)
            .withExamineeRelationship(mockExamineeRelationships)
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
                assertThat(attribute1.getContext().value()).isEqualTo(examineeAttribute.getContext().name());
                assertThat(attribute1.getContextDate()).isNotNull();
            }
        }

        assertThat(attribute1.getValue()).isEqualTo(examineeAttribute.getValue());
        assertThat(attribute1.getContext().value()).isEqualTo(examineeAttribute.getContext().name());
        assertThat(attribute1.getContextDate()).isNotNull();

        assertThat(relationship1.getValue()).isEqualTo(examineeRelationship.getValue());
        assertThat(relationship1.getContext().value()).isEqualTo(examineeRelationship.getContext().name());
        assertThat(relationship1.getContextDate()).isNotNull();

        TDSReport.Examinee.ExamineeAttribute dateAttribute =
            (TDSReport.Examinee.ExamineeAttribute)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeAttribute
                    && ((TDSReport.Examinee.ExamineeAttribute)attrOrRel).getName().equalsIgnoreCase("Birthdate"))
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

        TDSReport.Examinee.ExamineeRelationship districtIdAttribute =
            (TDSReport.Examinee.ExamineeRelationship)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeRelationship
                    && ((TDSReport.Examinee.ExamineeRelationship)attrOrRel).getName().equalsIgnoreCase("ResponsibleDistrictIdentifier"))
                .findFirst().get();
        assertThat(districtIdAttribute.getValue()).isEqualTo("Sweetwater");

        TDSReport.Examinee.ExamineeRelationship districtNameAttribute =
            (TDSReport.Examinee.ExamineeRelationship)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeRelationship
                    && ((TDSReport.Examinee.ExamineeRelationship)attrOrRel).getName().equalsIgnoreCase("OrganizationName"))
                .findFirst().get();
        assertThat(districtNameAttribute.getValue()).isEqualTo("District 9");

        TDSReport.Examinee.ExamineeRelationship schoolIdRelationship =
            (TDSReport.Examinee.ExamineeRelationship)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeRelationship
                    && ((TDSReport.Examinee.ExamineeRelationship)attrOrRel).getName().equalsIgnoreCase("ResponsibleInstitutionIdentifier"))
                .findFirst().get();
        assertThat(schoolIdRelationship.getValue()).isEqualTo("Hilltop");

        TDSReport.Examinee.ExamineeRelationship schoolNameRelationship =
            (TDSReport.Examinee.ExamineeRelationship)examinee.getExamineeAttributeOrExamineeRelationship().stream()
                .filter(attrOrRel -> attrOrRel instanceof TDSReport.Examinee.ExamineeRelationship
                    && ((TDSReport.Examinee.ExamineeRelationship)attrOrRel).getName().equalsIgnoreCase("NameOfInstitution"))
                .findFirst().get();
        assertThat(schoolNameRelationship.getValue()).isEqualTo("School of Rock");


    }

    private List<ExamineeAttribute> getExamineeAttributes() {
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
        return mockExamineeAttributes;
    }

    private List<ExamineeRelationship> getExamineeRelationships() {
        List<ExamineeRelationship> mockExamineeRelationships = randomListOf(2, ExamineeRelationship.class);
        mockExamineeRelationships.add(
            new ExamineeRelationship.Builder()
                .withExamId(UUID.randomUUID())
                .withName("DistrictID")
                .withValue("Sweetwater")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeRelationships.add(
            new ExamineeRelationship.Builder()
                .withExamId(UUID.randomUUID())
                .withName("SchoolName")
                .withValue("School of Rock")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeRelationships.add(
            new ExamineeRelationship.Builder()
                .withExamId(UUID.randomUUID())
                .withName("SchoolID")
                .withValue("Hilltop")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        mockExamineeRelationships.add(
            new ExamineeRelationship.Builder()
                .withExamId(UUID.randomUUID())
                .withName("DistrictName")
                .withValue("District 9")
                .withContext(ExamineeContext.INITIAL)
                .withCreatedAt(Instant.now())
                .build()
        );
        return mockExamineeRelationships;
    }
}
