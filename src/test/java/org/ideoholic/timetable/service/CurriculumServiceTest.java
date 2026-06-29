package org.ideoholic.timetable.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.ideoholic.timetable.dto.CurriculumRequest;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CurriculumServiceTest {

    private CurriculumRepository curriculumRepository;

    private CurriculumSubjectRepository curriculumSubjectRepository;

    private ClassMasterRepository classMasterRepository;

    private AcademicYearRepository academicYearRepository;

    private CurriculumService service;

    @BeforeEach
    void setUp() {
        curriculumRepository = mock(CurriculumRepository.class);
        curriculumSubjectRepository = mock(CurriculumSubjectRepository.class);
        classMasterRepository = mock(ClassMasterRepository.class);
        academicYearRepository = mock(AcademicYearRepository.class);
        service = new CurriculumService(
                curriculumRepository,
                curriculumSubjectRepository,
                classMasterRepository,
                academicYearRepository);
    }

    @Test
    void createRejectsDuplicateCurriculumForClassAndAcademicYear() {
        CurriculumRequest request = request();
        ClassMaster classMaster = classMaster();
        AcademicYear academicYear = academicYear();

        when(classMasterRepository.findById(1L)).thenReturn(Optional.of(classMaster));
        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(academicYear));
        when(curriculumRepository.findByClassMasterAndAcademicYear(classMaster, academicYear))
                .thenReturn(Optional.of(new Curriculum()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createRejectsInvalidClass() {
        CurriculumRequest request = request();
        when(classMasterRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createRejectsInvalidAcademicYear() {
        CurriculumRequest request = request();
        ClassMaster classMaster = classMaster();

        when(classMasterRepository.findById(1L)).thenReturn(Optional.of(classMaster));
        when(academicYearRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createSavesValidCurriculum() {
        CurriculumRequest request = request();
        ClassMaster classMaster = classMaster();
        AcademicYear academicYear = academicYear();

        when(classMasterRepository.findById(1L)).thenReturn(Optional.of(classMaster));
        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(academicYear));
        when(curriculumRepository.findByClassMasterAndAcademicYear(classMaster, academicYear))
                .thenReturn(Optional.empty());
        when(curriculumRepository.save(org.mockito.ArgumentMatchers.any(Curriculum.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Curriculum saved = service.create(request);

        assertEquals("Class 8 Curriculum", saved.getCurriculumName());
        verify(curriculumRepository).save(saved);
    }

    private CurriculumRequest request() {
        CurriculumRequest request = new CurriculumRequest();
        request.setCurriculumName("Class 8 Curriculum");
        request.setClassId(1L);
        request.setAcademicYearId(1L);
        request.setActive(Boolean.TRUE);
        return request;
    }

    private ClassMaster classMaster() {
        ClassMaster classMaster = new ClassMaster();
        classMaster.setId(1L);
        classMaster.setClassName("Class 8");
        return classMaster;
    }

    private AcademicYear academicYear() {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setId(1L);
        academicYear.setYearName("2026-27");
        return academicYear;
    }
}
