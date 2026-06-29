package org.ideoholic.timetable.engine.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ideoholic.timetable.engine.strategy.models.GenerationContext;
import org.ideoholic.timetable.engine.strategy.models.RemainingDemand;
import org.ideoholic.timetable.engine.strategy.models.SubjectPriority;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.repository.CurriculumRepository;
import org.ideoholic.timetable.repository.CurriculumSubjectRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurriculumGenerationStrategy
        implements GenerationStrategy {

    private final CurriculumRepository curriculumRepository;

    private final CurriculumSubjectRepository curriculumSubjectRepository;

    private final RemainingDemandTracker remainingDemandTracker;

    private final DailyDistributionTracker dailyDistributionTracker;

    private final WeeklyDistributionTracker weeklyDistributionTracker;

    private final SubjectPriorityCalculator subjectPriorityCalculator;

    @Override
    public List<SubjectPriority> prioritize(
            GenerationContext context) {

        Map<Long, CurriculumSubject> curriculumSubjectsBySubjectId =
                curriculumSubjectsBySubjectId(context.getSection());

        return availableSubjects(context, curriculumSubjectsBySubjectId)
                .stream()
                .filter(subject -> hasAvailableTeacherForPeriod(subject, context))
                .map(subject -> priority(subject, curriculumSubjectsBySubjectId, context))
                .filter(candidate -> candidate.getRemainingDemand().getRemainingPeriods() > 0)
                .filter(candidate -> dailyDistributionTracker.respectsDailyLimit(
                        candidate.getSubject(),
                        context.getDaySubjectCount()))
                .sorted(Comparator
                        .comparingInt(SubjectPriority::getPriority)
                        .reversed()
                        .thenComparingLong(candidate -> candidate.getSubject().getId()))
                .collect(Collectors.toList());
    }

    private List<Subject> availableSubjects(
            GenerationContext context,
            Map<Long, CurriculumSubject> curriculumSubjectsBySubjectId) {

        List<Subject> availableSubjects = context.getAvailableSubjects() == null
                ? new ArrayList<>()
                : context.getAvailableSubjects();

        if (curriculumSubjectsBySubjectId.isEmpty()) {
            return availableSubjects;
        }

        return availableSubjects.stream()
                .filter(subject -> subject != null && subject.getId() != null)
                .filter(subject -> curriculumSubjectsBySubjectId.containsKey(subject.getId()))
                .collect(Collectors.toList());
    }

    private SubjectPriority priority(
            Subject subject,
            Map<Long, CurriculumSubject> curriculumSubjectsBySubjectId,
            GenerationContext context) {

        CurriculumSubject curriculumSubject = curriculumSubjectsBySubjectId.get(subject.getId());
        Integer curriculumWeeklyPeriods = curriculumSubject == null
                ? null
                : curriculumSubject.getWeeklyPeriods();
        String categoryName = categoryName(curriculumSubject);

        RemainingDemand remainingDemand = remainingDemandTracker.calculate(
                subject,
                curriculumWeeklyPeriods,
                context.getWeeklySubjectCount());

        int priority = subjectPriorityCalculator.calculate(
                subject,
                categoryName,
                remainingDemand,
                context,
                dailyDistributionTracker,
                weeklyDistributionTracker);

        return new SubjectPriority(subject, priority, remainingDemand, categoryName);
    }

    private Map<Long, CurriculumSubject> curriculumSubjectsBySubjectId(
            Section section) {

        Map<Long, CurriculumSubject> result = new HashMap<>();

        if (section == null
                || section.getClassMaster() == null
                || section.getAcademicYear() == null) {
            return result;
        }

        Curriculum curriculum = curriculumRepository.findByClassMasterAndAcademicYear(
                        section.getClassMaster(),
                        section.getAcademicYear())
                .filter(item -> !Boolean.FALSE.equals(item.getActive()))
                .orElse(null);

        if (curriculum == null) {
            return result;
        }

        for (CurriculumSubject curriculumSubject : curriculumSubjectRepository
                .findByCurriculumOrderByDisplayOrderAscIdAsc(curriculum)) {
            if (Boolean.FALSE.equals(curriculumSubject.getActive())
                    || curriculumSubject.getSubject() == null
                    || curriculumSubject.getSubject().getId() == null) {
                continue;
            }

            result.put(curriculumSubject.getSubject().getId(), curriculumSubject);
        }

        return result;
    }

    private String categoryName(
            CurriculumSubject curriculumSubject) {

        if (curriculumSubject == null || curriculumSubject.getCategory() == null) {
            return null;
        }

        return curriculumSubject.getCategory().getCategoryName();
    }

    private boolean hasAvailableTeacherForPeriod(
            Subject subject,
            GenerationContext context) {

        if (subject == null || subject.getId() == null) {
            return false;
        }

        Map<Long, List<Teacher>> teachersBySubjectId = context.getTeachersBySubjectId();
        if (teachersBySubjectId == null) {
            return false;
        }

        return teachersBySubjectId.getOrDefault(subject.getId(), new ArrayList<>())
                .stream()
                .anyMatch(teacher -> teacher != null
                        && teacher.getId() != null
                        && !context.getOccupiedTeacherIds().contains(teacher.getId()));
    }
}
