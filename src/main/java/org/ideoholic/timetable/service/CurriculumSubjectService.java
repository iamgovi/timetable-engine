package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.dto.CurriculumSubjectRequest;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.SubjectCategory;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.ideoholic.timetable.repository.SubjectCategoryRepository;
import org.ideoholic.timetable.repository.SubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurriculumSubjectService {

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final CurriculumRepository curriculumRepository;

    private final SubjectRepository subjectRepository;

    private final SubjectCategoryRepository subjectCategoryRepository;

    public CurriculumSubject create(
            Long curriculumId,
            CurriculumSubjectRequest request) {

        Curriculum curriculum = findCurriculum(curriculumId);
        Subject subject = findSubject(request.getSubjectId());

        curriculumSubjectRepository.findByCurriculumAndSubject(curriculum, subject)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Subject already exists in curriculum");
                });

        CurriculumSubject curriculumSubject = new CurriculumSubject();
        apply(curriculumSubject, curriculum, subject, request);
        return curriculumSubjectRepository.save(curriculumSubject);
    }

    public CurriculumSubject update(
            Long id,
            CurriculumSubjectRequest request) {

        CurriculumSubject curriculumSubject = findById(id);
        Subject subject = findSubject(request.getSubjectId());

        curriculumSubjectRepository.findByCurriculumAndSubject(
                curriculumSubject.getCurriculum(),
                subject)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Subject already exists in curriculum");
                });

        apply(curriculumSubject, curriculumSubject.getCurriculum(), subject, request);
        return curriculumSubjectRepository.save(curriculumSubject);
    }

    public List<CurriculumSubject> findByCurriculum(
            Long curriculumId) {

        return curriculumSubjectRepository.findByCurriculumOrderByDisplayOrderAscIdAsc(
                findCurriculum(curriculumId));
    }

    public CurriculumSubject findById(
            Long id) {

        return curriculumSubjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curriculum subject not found"));
    }

    public void delete(
            Long id) {

        curriculumSubjectRepository.delete(findById(id));
    }

    private void apply(
            CurriculumSubject curriculumSubject,
            Curriculum curriculum,
            Subject subject,
            CurriculumSubjectRequest request) {

        curriculumSubject.setCurriculum(curriculum);
        curriculumSubject.setSubject(subject);
        curriculumSubject.setCategory(findCategory(request.getCategoryId()));
        curriculumSubject.setWeeklyPeriods(request.getWeeklyPeriods());
        curriculumSubject.setDailyPeriodLimit(request.getDailyPeriodLimit());
        curriculumSubject.setRequirementType(request.getRequirementType());
        curriculumSubject.setStreamName(request.getStreamName());
        curriculumSubject.setElectiveGroup(request.getElectiveGroup());
        curriculumSubject.setOptionalSubject(request.getOptionalSubject() == null
                ? Boolean.FALSE
                : request.getOptionalSubject());
        curriculumSubject.setDisplayOrder(request.getDisplayOrder());
        curriculumSubject.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
    }

    private Curriculum findCurriculum(
            Long curriculumId) {

        return curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curriculum not found"));
    }

    private Subject findSubject(
            Long subjectId) {

        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Subject not found"));
    }

    private SubjectCategory findCategory(
            Long categoryId) {

        if (categoryId == null) {
            return null;
        }

        return subjectCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Subject category not found"));
    }
}
