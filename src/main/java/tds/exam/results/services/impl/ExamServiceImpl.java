package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import tds.exam.ExpandableExam;
import tds.exam.results.repositories.ExamRepository;
import tds.exam.results.services.ExamService;

@Service
public class ExamServiceImpl implements ExamService {
    final ExamRepository examRepository;

    @Autowired
    public ExamServiceImpl(final ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public ExpandableExam findExpandableExam(final UUID examId) {
        return examRepository.findExpandableExam(examId);
    }

    @Override
    public void updateStatus(final UUID examId, final String status) {
        examRepository.updateStatus(examId, status);
    }
}
