package org.ideoholic.timetable.engine.feasibility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.FeasibilityRequest;
import org.ideoholic.timetable.engine.planning.AcademicPlanningService;
import org.ideoholic.timetable.engine.planning.models.PlanningFilter;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.repository.AcademicYearRepository;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.ideoholic.timetable.repository.PeriodRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.TeacherAvailabilityRepository;
import org.ideoholic.timetable.repository.TeacherSubjectMappingRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.springframework.stereotype.Component;

@Component
public class FeasibilityEngine {

    private final List<FeasibilityValidator> validators;

    private final AcademicYearRepository academicYearRepository;

    private final ClassMasterRepository classMasterRepository;

    private final SectionRepository sectionRepository;

    private final WorkingDayRepository workingDayRepository;

    private final PeriodRepository periodRepository;

    private final CurriculumRepository curriculumRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final TeacherSubjectMappingRepository teacherSubjectMappingRepository;

    private final TeacherAvailabilityRepository teacherAvailabilityRepository;

    private final AcademicPlanningService academicPlanningService;

    public FeasibilityEngine(
            List<FeasibilityValidator> validators,
            AcademicYearRepository academicYearRepository,
            ClassMasterRepository classMasterRepository,
            SectionRepository sectionRepository,
            WorkingDayRepository workingDayRepository,
            PeriodRepository periodRepository,
            CurriculumRepository curriculumRepository,
            CurriculumSubjectRepository curriculumSubjectRepository,
            TeacherSubjectMappingRepository teacherSubjectMappingRepository,
            TeacherAvailabilityRepository teacherAvailabilityRepository,
            AcademicPlanningService academicPlanningService) {

        this.validators = validators;
        this.academicYearRepository = academicYearRepository;
        this.classMasterRepository = classMasterRepository;
        this.sectionRepository = sectionRepository;
        this.workingDayRepository = workingDayRepository;
        this.periodRepository = periodRepository;
        this.curriculumRepository = curriculumRepository;
        this.curriculumSubjectRepository = curriculumSubjectRepository;
        this.teacherSubjectMappingRepository = teacherSubjectMappingRepository;
        this.teacherAvailabilityRepository = teacherAvailabilityRepository;
        this.academicPlanningService = academicPlanningService;
    }

    public FeasibilityReport validate(
            FeasibilityRequest request) {

        FeasibilityContext context = context(request == null ? new FeasibilityRequest() : request);
        FeasibilityReport report = new FeasibilityReport();
        ValidationSummary summary = new ValidationSummary();

        summary.setTotalChecks(validators.size());

        for (FeasibilityValidator validator : validators) {
            FeasibilityCheckResult result = validator.validate(context);
            report.getErrors().addAll(result.getErrors());
            report.getWarnings().addAll(result.getWarnings());

            if (!result.getErrors().isEmpty()) {
                summary.setFailedChecks(summary.getFailedChecks() + 1);
            } else if (!result.getWarnings().isEmpty()) {
                summary.setWarningChecks(summary.getWarningChecks() + 1);
            } else {
                summary.setPassedChecks(summary.getPassedChecks() + 1);
            }
        }

        summary.setErrors(report.getErrors().size());
        summary.setWarnings(report.getWarnings().size());
        report.setFeasible(report.getErrors().isEmpty());
        report.setSummary(summary);
        report.setRecommendations(recommendations(report));
        return report;
    }

    private FeasibilityContext context(
            FeasibilityRequest request) {

        FeasibilityContext context = new FeasibilityContext();
        context.setRequest(request);
        AcademicYear academicYear = resolveAcademicYear(request);
        context.setAcademicYear(academicYear);
        context.setSections(resolveSections(request, academicYear));
        context.setClasses(resolveClasses(request, academicYear, context.getSections()));
        context.setWorkingDays(resolveWorkingDays(request));
        context.setPeriods(periodRepository.findAll()
                .stream()
                .filter(period -> !Boolean.TRUE.equals(period.getBreakPeriod()))
                .sorted(Comparator.comparingInt(period -> period.getPeriodNumber() == null
                        ? Integer.MAX_VALUE
                        : period.getPeriodNumber()))
                .collect(Collectors.toList()));
        context.setCurricula(resolveCurricula(academicYear, context.getClasses()));
        context.setCurriculumSubjectsByCurriculumId(subjectsByCurriculumId(context.getCurricula()));
        context.setTeacherSubjectMappings(teacherSubjectMappingRepository.findAll());
        context.setTeacherAvailabilities(teacherAvailabilityRepository.findAll());
        context.setAcademicPlan(academicPlanningService.plan(planningFilter(request, academicYear)));
        return context;
    }

    private AcademicYear resolveAcademicYear(
            FeasibilityRequest request) {

        if (request.getAcademicYearId() != null) {
            return academicYearRepository.findById(request.getAcademicYearId())
                    .orElse(null);
        }

        return academicYearRepository.findByCurrentTrue()
                .stream()
                .min(Comparator.comparing(AcademicYear::getId))
                .orElseGet(() -> academicYearRepository.findAll()
                        .stream()
                        .min(Comparator.comparing(AcademicYear::getId))
                        .orElse(null));
    }

    private List<Section> resolveSections(
            FeasibilityRequest request,
            AcademicYear academicYear) {

        if (request.getSectionIds() != null && !request.getSectionIds().isEmpty()) {
            return sectionRepository.findAllById(request.getSectionIds());
        }

        if (academicYear != null
                && request.getClassIds() != null
                && !request.getClassIds().isEmpty()) {
            return sectionRepository.findByAcademicYearIdAndClassMasterIdIn(
                    academicYear.getId(),
                    request.getClassIds());
        }

        if (academicYear == null) {
            return new ArrayList<>();
        }

        return sectionRepository.findAll()
                .stream()
                .filter(section -> section.getAcademicYear() != null)
                .filter(section -> academicYear.getId().equals(section.getAcademicYear().getId()))
                .collect(Collectors.toList());
    }

    private List<ClassMaster> resolveClasses(
            FeasibilityRequest request,
            AcademicYear academicYear,
            List<Section> sections) {

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            return classMasterRepository.findAllById(request.getClassIds());
        }

        if (request.getSectionIds() != null && !request.getSectionIds().isEmpty()) {
            Map<Long, ClassMaster> classesById = new LinkedHashMap<>();
            for (Section section : sections) {
                if (section.getClassMaster() != null) {
                    classesById.put(section.getClassMaster().getId(), section.getClassMaster());
                }
            }
            return new ArrayList<>(classesById.values());
        }

        if (academicYear == null) {
            return new ArrayList<>();
        }

        return curriculumRepository.findByAcademicYearId(academicYear.getId())
                .stream()
                .filter(curriculum -> curriculum.getClassMaster() != null)
                .map(Curriculum::getClassMaster)
                .collect(Collectors.toList());
    }

    private List<org.ideoholic.timetable.entity.WorkingDay> resolveWorkingDays(
            FeasibilityRequest request) {

        if (request.getWorkingDayIds() != null && !request.getWorkingDayIds().isEmpty()) {
            return workingDayRepository.findAllById(request.getWorkingDayIds())
                    .stream()
                    .filter(day -> !Boolean.FALSE.equals(day.getWorking()))
                    .sorted(Comparator.comparingLong(day -> day.getId() == null
                            ? Long.MAX_VALUE
                            : day.getId()))
                    .collect(Collectors.toList());
        }

        return workingDayRepository.findAll()
                .stream()
                .filter(day -> !Boolean.FALSE.equals(day.getWorking()))
                .sorted(Comparator.comparingLong(day -> day.getId() == null
                        ? Long.MAX_VALUE
                        : day.getId()))
                .collect(Collectors.toList());
    }

    private List<Curriculum> resolveCurricula(
            AcademicYear academicYear,
            List<ClassMaster> classes) {

        if (academicYear == null || classes.isEmpty()) {
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
                .collect(Collectors.toList());
    }

    private Map<Long, List<CurriculumSubject>> subjectsByCurriculumId(
            List<Curriculum> curricula) {

        Map<Long, List<CurriculumSubject>> result = new HashMap<>();
        for (Curriculum curriculum : curricula) {
            result.put(curriculum.getId(),
                    curriculumSubjectRepository.findByCurriculumOrderByDisplayOrderAscIdAsc(
                            curriculum));
        }
        return result;
    }

    private PlanningFilter planningFilter(
            FeasibilityRequest request,
            AcademicYear academicYear) {

        PlanningFilter filter = new PlanningFilter();
        filter.setAcademicYearId(academicYear == null ? request.getAcademicYearId() : academicYear.getId());
        filter.setClassIds(request.getClassIds());
        filter.setSectionIds(request.getSectionIds());
        return filter;
    }

    private List<String> recommendations(
            FeasibilityReport report) {

        return report.getErrors()
                .stream()
                .map(ValidationIssue::getRecommendation)
                .filter(recommendation -> recommendation != null && !recommendation.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
