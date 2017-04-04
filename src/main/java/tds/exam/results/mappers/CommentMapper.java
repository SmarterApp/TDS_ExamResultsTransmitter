package tds.exam.results.mappers;

import java.util.List;

import tds.exam.ExpandableExam;
import tds.exam.results.mappers.utils.JaxbMapperUtils;
import tds.exam.results.trt.TDSReport;

/**
 * A mapper class for mapping examinee notes to the JAXB {@link tds.exam.results.trt.TDSReport.Comment} object
 */
public class CommentMapper {
    public static void mapComments(final List<TDSReport.Comment> comments, final ExpandableExam expandableExam) {
        expandableExam.getExamineeNotes().forEach(note -> {
            TDSReport.Comment comment = new TDSReport.Comment();
            comment.setContext(note.getContext().toString());
            comment.setItemPosition(String.valueOf(note.getItemPosition()));
            comment.setDate(JaxbMapperUtils.convertInstantToGregorianCalendar(note.getCreatedAt()));
            comment.setContent(note.getNote());
            comments.add(comment);
        });
    }
}
