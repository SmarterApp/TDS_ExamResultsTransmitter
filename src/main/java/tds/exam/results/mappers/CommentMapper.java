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
