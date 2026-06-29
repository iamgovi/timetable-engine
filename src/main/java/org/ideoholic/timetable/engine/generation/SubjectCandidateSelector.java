package org.ideoholic.timetable.engine.generation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.springframework.stereotype.Component;

@Component
class SubjectCandidateSelector {

    private static final int REMAINING_DEMAND_WEIGHT = 100;
    private static final int ALLOCATIONS_TODAY_PENALTY = 1000;
    private static final int CONSECUTIVE_SUBJECT_PENALTY = 500;
    private static final int PREVIOUS_DAY_SAME_PERIOD_PENALTY = 100;

    List<SubjectCandidate> selectCandidates(
            List<Subject> availableSubjects,
            Map<Long, List<Teacher>> teachersBySubjectId,
            Set<Long> occupiedTeacherIds,
            Map<Long, Integer> weeklySubjectCount,
            Map<Long, Integer> daySubjectCount,
            Long previousPeriodSubjectId,
            Long previousDayPeriodSubjectId) {

        return availableSubjects.stream()
                .filter(subject -> hasRemainingWeeklyDemand(subject, weeklySubjectCount))
                .filter(subject -> respectsDailyLimit(subject, daySubjectCount))
                .filter(subject -> hasAvailableTeacherForPeriod(subject, teachersBySubjectId,
                        occupiedTeacherIds))
                .map(subject -> new SubjectCandidate(
                        subject,
                        calculateSubjectPriority(
                                subject,
                                weeklySubjectCount,
                                daySubjectCount,
                                previousPeriodSubjectId,
                                previousDayPeriodSubjectId)))
                .sorted(Comparator
                        .comparingInt(SubjectCandidate::getPriority)
                        .reversed()
                        .thenComparingLong(candidate -> candidate.getSubject().getId()))
                .collect(Collectors.toList());
    }

    private int calculateSubjectPriority(
            Subject subject,
            Map<Long, Integer> weeklySubjectCount,
            Map<Long, Integer> daySubjectCount,
            Long previousPeriodSubjectId,
            Long previousDayPeriodSubjectId) {

        int priority = remainingDemand(subject, weeklySubjectCount) * REMAINING_DEMAND_WEIGHT;
        priority -= daySubjectCount.getOrDefault(subject.getId(), 0) * ALLOCATIONS_TODAY_PENALTY;

        if (isSameSubject(previousPeriodSubjectId, subject)) {
            priority -= CONSECUTIVE_SUBJECT_PENALTY;
        }

        if (isSameSubject(previousDayPeriodSubjectId, subject)) {
            priority -= PREVIOUS_DAY_SAME_PERIOD_PENALTY;
        }

        return priority;
    }

    private boolean isSameSubject(
            Long subjectId,
            Subject subject) {

        return subjectId != null
                && subject != null
                && subject.getId() != null
                && subjectId.equals(subject.getId());
    }

    private boolean hasRemainingWeeklyDemand(
            Subject subject,
            Map<Long, Integer> allocatedCount) {

        return remainingDemand(subject, allocatedCount) > 0;
    }

    private int remainingDemand(
            Subject subject,
            Map<Long, Integer> allocatedCount) {

        if (subject == null || subject.getWeeklyPeriods() == null || subject.getId() == null) {
            return 0;
        }

        return subject.getWeeklyPeriods() - allocatedCount.getOrDefault(subject.getId(), 0);
    }

    private boolean respectsDailyLimit(
            Subject subject,
            Map<Long, Integer> dayCount) {

        if (subject == null || subject.getDailyPeriods() == null) {
            return true;
        }

        return dayCount.getOrDefault(subject.getId(), 0) < subject.getDailyPeriods();
    }

    private boolean hasAvailableTeacherForPeriod(
            Subject subject,
            Map<Long, List<Teacher>> teachersBySubjectId,
            Set<Long> occupiedTeacherIds) {

        return teachersBySubjectId.getOrDefault(subject.getId(), new ArrayList<>()).stream()
                .anyMatch(teacher -> !occupiedTeacherIds.contains(teacher.getId()));
    }
}
