package org.ideoholic.timetable.engine.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.CurriculumDemandResult;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.engine.planning.models.TeacherCapacity;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.TeacherRepository;
import org.ideoholic.timetable.repository.TeacherSubjectMappingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicPlanningService {

    private final AcademicYearRepository academicYearRepository;

    private final ClassMasterRepository classMasterRepository;

    private final SectionRepository sectionRepository;

    private final CurriculumRepository curriculumRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final TeacherRepository teacherRepository;

    private final TeacherSubjectMappingRepository teacherSubjectMappingRepository;

    private final CurriculumDemandCalculator curriculumDemandCalculator;

    private final TeacherCapacityCalculator teacherCapacityCalculator;

    private final TeacherRequirementAnalyzer teacherRequirementAnalyzer;

    private final TeacherUtilizationCalculator teacherUtilizationCalculator;

    private final PlanningReportBuilder planningReportBuilder;

    public AcademicPlan plan(
            PlanningFilter filter) {

        AcademicYear academicYear = resolveAcademicYear(filter);
        List<Section> sections = resolveSections(filter, academicYear);
        List<ClassMaster> classes = resolveClasses(filter, academicYear, sections);
        List<Curriculum> curricula = resolveCurricula(academicYear, classes);
        Map<Long, List<CurriculumSubject>> subjectsByCurriculumId = subjectsByCurriculumId(curricula);
        Map<Long, Integer> sectionCountByClassId = sectionCountByClassId(classes, sections);

        CurriculumDemandResult demandResult = curriculumDemandCalculator.calculate(
                curricula,
                subjectsByCurriculumId,
                sectionCountByClassId);

        List<Teacher> teachers = teacherRepository.findAll();
        List<TeacherSubjectMapping> mappings = teacherSubjectMappingRepository.findAll();
        List<TeacherCapacity> capacities = teacherCapacityCalculator.calculate(teachers);
        List<TeacherRequirement> requirements = teacherRequirementAnalyzer.analyze(
                demandResult.getSubjectDemands(),
                capacities,
                mappings);
        List<TeacherUtilization> utilizations = teacherUtilizationCalculator.calculate(
                demandResult.getSubjectDemands(),
                capacities,
                mappings);

        return planningReportBuilder.build(demandResult, requirements, utilizations);
    }

    private AcademicYear resolveAcademicYear(
            PlanningFilter filter) {

        if (filter != null && filter.getAcademicYearId() != null) {
            return academicYearRepository.findById(filter.getAcademicYearId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Academic year not found"));
        }

        return academicYearRepository.findByCurrentTrue()
                .stream()
                .min(Comparator.comparing(AcademicYear::getId))
                .orElseGet(() -> academicYearRepository.findAll()
                        .stream()
                        .min(Comparator.comparing(AcademicYear::getId))
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Academic year not found")));
    }

    private List<Section> resolveSections(
            PlanningFilter filter,
            AcademicYear academicYear) {

        if (filter != null
                && filter.getSectionIds() != null
                && !filter.getSectionIds().isEmpty()) {
            return sectionRepository.findAllById(filter.getSectionIds());
        }

        if (filter != null
                && filter.getClassIds() != null
                && !filter.getClassIds().isEmpty()) {
            return sectionRepository.findByAcademicYearIdAndClassMasterIdIn(
                    academicYear.getId(),
                    filter.getClassIds());
        }

        return sectionRepository.findAll()
                .stream()
                .filter(section -> section.getAcademicYear() != null)
                .filter(section -> academicYear.getId().equals(section.getAcademicYear().getId()))
                .collect(Collectors.toList());
    }

    private List<ClassMaster> resolveClasses(
            PlanningFilter filter,
            AcademicYear academicYear,
            List<Section> sections) {

        if (filter != null
                && filter.getClassIds() != null
                && !filter.getClassIds().isEmpty()) {
            return classMasterRepository.findAllById(filter.getClassIds())
                    .stream()
                    .sorted(Comparator.comparing(ClassMaster::getId))
                    .collect(Collectors.toList());
        }

        if (filter != null
                && filter.getSectionIds() != null
                && !filter.getSectionIds().isEmpty()) {
            Map<Long, ClassMaster> classesById = new LinkedHashMap<>();
            sections.stream()
                    .filter(section -> section.getClassMaster() != null)
                    .forEach(section -> classesById.put(
                            section.getClassMaster().getId(),
                            section.getClassMaster()));
            return new ArrayList<>(classesById.values());
        }

        return curriculumRepository.findByAcademicYearId(academicYear.getId())
                .stream()
                .filter(curriculum -> curriculum.getClassMaster() != null)
                .map(Curriculum::getClassMaster)
                .sorted(Comparator.comparing(ClassMaster::getId))
                .collect(Collectors.toList());
    }

    private List<Curriculum> resolveCurricula(
            AcademicYear academicYear,
            List<ClassMaster> classes) {

        if (classes.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> classIds = classes.stream()
                .map(ClassMaster::getId)
                .collect(Collectors.toList());

        return curriculumRepository.findByAcademicYearIdAndClassMasterIdIn(
                academicYear.getId(),
                classIds)
                .stream()
                .filter(curriculum -> !Boolean.FALSE.equals(curriculum.getActive()))
                .sorted(Comparator.comparing(curriculum -> curriculum.getClassMaster().getId()))
                .collect(Collectors.toList());
    }

    private Map<Long, List<CurriculumSubject>> subjectsByCurriculumId(
            List<Curriculum> curricula) {

        Map<Long, List<CurriculumSubject>> subjectsByCurriculumId = new HashMap<>();
        for (Curriculum curriculum : curricula) {
            subjectsByCurriculumId.put(
                    curriculum.getId(),
                    curriculumSubjectRepository.findByCurriculumOrderByDisplayOrderAscIdAsc(
                            curriculum));
        }
        return subjectsByCurriculumId;
    }

    private Map<Long, Integer> sectionCountByClassId(
            List<ClassMaster> classes,
            List<Section> sections) {

        Map<Long, Integer> sectionCountByClassId = classes.stream()
                .collect(Collectors.toMap(
                        ClassMaster::getId,
                        ignored -> 0,
                        Integer::sum,
                        LinkedHashMap::new));

        for (Section section : sections) {
            if (section.getClassMaster() == null) {
                continue;
            }

            Long classId = section.getClassMaster().getId();
            if (sectionCountByClassId.containsKey(classId)) {
                sectionCountByClassId.merge(classId, 1, Integer::sum);
            }
        }

        return sectionCountByClassId;
    }
}
