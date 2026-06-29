package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.dto.CurriculumRequest;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final ClassMasterRepository classMasterRepository;

    private final AcademicYearRepository academicYearRepository;

    public Curriculum create(
            CurriculumRequest request) {

        ClassMaster classMaster = findClass(request.getClassId());
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());

        curriculumRepository.findByClassMasterAndAcademicYear(classMaster, academicYear)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Curriculum already exists for class and academic year");
                });

        Curriculum curriculum = new Curriculum();
        apply(curriculum, request, classMaster, academicYear);
        return curriculumRepository.save(curriculum);
    }

    public Curriculum update(
            Long id,
            CurriculumRequest request) {

        Curriculum curriculum = findById(id);
        ClassMaster classMaster = findClass(request.getClassId());
        AcademicYear academicYear = findAcademicYear(request.getAcademicYearId());

        curriculumRepository.findByClassMasterAndAcademicYear(classMaster, academicYear)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Curriculum already exists for class and academic year");
                });

        apply(curriculum, request, classMaster, academicYear);
        return curriculumRepository.save(curriculum);
    }

    public List<Curriculum> findAll() {
        return curriculumRepository.findAll();
    }

    public Curriculum findById(
            Long id) {

        return curriculumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curriculum not found"));
    }

    public List<Curriculum> findByClassId(
            Long classId) {

        return curriculumRepository.findByClassMasterId(classId);
    }

    public List<Curriculum> findByAcademicYearId(
            Long academicYearId) {

        return curriculumRepository.findByAcademicYearId(academicYearId);
    }

    public Curriculum findByClassAndAcademicYear(
            Long classId,
            Long academicYearId) {

        return curriculumRepository.findByClassMasterAndAcademicYear(
                findClass(classId),
                findAcademicYear(academicYearId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curriculum not found"));
    }

    public void delete(
            Long id) {

        Curriculum curriculum = findById(id);
        if (curriculumSubjectRepository.existsByCurriculum(curriculum)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Curriculum has subjects and cannot be deleted");
        }

        curriculumRepository.delete(curriculum);
    }

    private void apply(
            Curriculum curriculum,
            CurriculumRequest request,
            ClassMaster classMaster,
            AcademicYear academicYear) {

        curriculum.setCurriculumName(request.getCurriculumName());
        curriculum.setClassMaster(classMaster);
        curriculum.setAcademicYear(academicYear);
        curriculum.setDescription(request.getDescription());
        curriculum.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());
    }

    private ClassMaster findClass(
            Long classId) {

        return classMasterRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Class not found"));
    }

    private AcademicYear findAcademicYear(
            Long academicYearId) {

        return academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Academic year not found"));
    }
}
