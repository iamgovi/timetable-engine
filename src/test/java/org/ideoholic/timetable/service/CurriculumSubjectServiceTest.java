package org.ideoholic.timetable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.ideoholic.timetable.dto.CurriculumSubjectRequest;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.SubjectCategory;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.ideoholic.timetable.repository.SubjectCategoryRepository;
import org.ideoholic.timetable.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CurriculumSubjectServiceTest {

    private CurriculumSubjectRepository curriculumSubjectRepository;

    private CurriculumRepository curriculumRepository;

    private SubjectRepository subjectRepository;

    private SubjectCategoryRepository subjectCategoryRepository;

    private CurriculumSubjectService service;

    @BeforeEach
    void setUp() {
        curriculumSubjectRepository = mock(CurriculumSubjectRepository.class);
        curriculumRepository = mock(CurriculumRepository.class);
        subjectRepository = mock(SubjectRepository.class);
        subjectCategoryRepository = mock(SubjectCategoryRepository.class);
        service = new CurriculumSubjectService(
                curriculumSubjectRepository,
                curriculumRepository,
                subjectRepository,
                subjectCategoryRepository);
    }

    @Test
    void createRejectsDuplicateCurriculumSubject() {
        Curriculum curriculum = curriculum();
        Subject subject = subject();
        CurriculumSubjectRequest request = request();

        when(curriculumRepository.findById(1L)).thenReturn(Optional.of(curriculum));
        when(subjectRepository.findById(10L)).thenReturn(Optional.of(subject));
        when(curriculumSubjectRepository.findByCurriculumAndSubject(curriculum, subject))
                .thenReturn(Optional.of(new CurriculumSubject()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(1L, request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createRejectsInvalidSubject() {
        CurriculumSubjectRequest request = request();
        when(curriculumRepository.findById(1L)).thenReturn(Optional.of(curriculum()));
        when(subjectRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(1L, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createRejectsInvalidCategory() {
        Curriculum curriculum = curriculum();
        Subject subject = subject();
        CurriculumSubjectRequest request = request();
        request.setCategoryId(20L);

        when(curriculumRepository.findById(1L)).thenReturn(Optional.of(curriculum));
        when(subjectRepository.findById(10L)).thenReturn(Optional.of(subject));
        when(curriculumSubjectRepository.findByCurriculumAndSubject(curriculum, subject))
                .thenReturn(Optional.empty());
        when(subjectCategoryRepository.findById(20L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(1L, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createSavesValidCurriculumSubject() {
        Curriculum curriculum = curriculum();
        Subject subject = subject();
        SubjectCategory category = category();
        CurriculumSubjectRequest request = request();
        request.setCategoryId(20L);

        when(curriculumRepository.findById(1L)).thenReturn(Optional.of(curriculum));
        when(subjectRepository.findById(10L)).thenReturn(Optional.of(subject));
        when(curriculumSubjectRepository.findByCurriculumAndSubject(curriculum, subject))
                .thenReturn(Optional.empty());
        when(subjectCategoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(curriculumSubjectRepository.save(org.mockito.ArgumentMatchers.any(CurriculumSubject.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CurriculumSubject saved = service.create(1L, request);

        assertEquals(Integer.valueOf(6), saved.getWeeklyPeriods());
        assertEquals(category, saved.getCategory());
    }

    private CurriculumSubjectRequest request() {
        CurriculumSubjectRequest request = new CurriculumSubjectRequest();
        request.setSubjectId(10L);
        request.setWeeklyPeriods(6);
        request.setRequirementType("CORE");
        request.setOptionalSubject(Boolean.FALSE);
        request.setActive(Boolean.TRUE);
        return request;
    }

    private Curriculum curriculum() {
        Curriculum curriculum = new Curriculum();
        curriculum.setId(1L);
        return curriculum;
    }

    private Subject subject() {
        Subject subject = new Subject();
        subject.setId(10L);
        subject.setSubjectName("Mathematics");
        return subject;
    }

    private SubjectCategory category() {
        SubjectCategory category = new SubjectCategory();
        category.setId(20L);
        category.setCategoryName("Core");
        return category;
    }
}
