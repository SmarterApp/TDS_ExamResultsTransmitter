package tds.exam.results.mappers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import tds.exam.Exam;
import tds.exam.ExamineeNote;
import tds.exam.ExpandableExam;
import tds.exam.results.trt.TDSReport;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    @Test
    public void shouldMapExamineeNotesToComments() {
        ExpandableExam expandableExam = new ExpandableExam.Builder(random(Exam.class))
            .withExamineeNotes(randomListOf(2, ExamineeNote.class))
            .build();
        List<TDSReport.Comment> comments = new ArrayList<>();

        CommentMapper.mapComments(comments, expandableExam);

        assertThat(comments.size()).isEqualTo(expandableExam.getExamineeNotes().size());

        ExamineeNote examineeNote1 = expandableExam.getExamineeNotes().get(0);
        ExamineeNote examineeNote2 = expandableExam.getExamineeNotes().get(1);

        TDSReport.Comment comment1 = null;
        TDSReport.Comment comment2 = null;

        for (TDSReport.Comment comment : comments) {
            if (Integer.parseInt(comment.getItemPosition()) == examineeNote1.getItemPosition()) {
                comment1 = comment;
            } else if (Integer.parseInt(comment.getItemPosition()) == examineeNote2.getItemPosition()) {
                comment2 = comment;
            }
        }

        assertThat(comment1.getContent()).isEqualTo(examineeNote1.getNote());
        assertThat(comment1.getContext()).isEqualTo(examineeNote1.getContext().toString());
        assertThat(comment1.getDate()).isNotNull();

        assertThat(comment2.getContent()).isEqualTo(examineeNote2.getNote());
        assertThat(comment2.getContext()).isEqualTo(examineeNote2.getContext().toString());
        assertThat(comment2.getDate()).isNotNull();
    }
}