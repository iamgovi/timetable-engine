package org.ideoholic.timetable.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ideoholic.timetable.dto.TimetableAllocationRequest;
import org.ideoholic.timetable.dto.TimetableAllocationResponse;
import org.ideoholic.timetable.dto.TimetableGenerationRequest;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.TimetableAssignment;
import org.ideoholic.timetable.entity.WorkingDay;
import org.ideoholic.timetable.repository.PeriodRepository;
import org.ideoholic.timetable.repository.SectionRepository;
import org.ideoholic.timetable.repository.TeacherRepository;
import org.ideoholic.timetable.repository.TimetableAssignmentRepository;
import org.ideoholic.timetable.repository.WorkingDayRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableGenerationService {

    private final TimetableAllocationService allocationService;

    private final TeacherRepository teacherRepository;

    private final SectionRepository sectionRepository;

    private final PeriodRepository periodRepository;

    private final WorkingDayRepository workingDayRepository;

    private final TimetableAssignmentRepository assignmentRepository;

    public List<TimetableAssignment> generateMondayTimetable(
            TimetableGenerationRequest request) {

        String workingDayName = request.getWorkingDayName();
        if (workingDayName == null || workingDayName.isBlank()) {
            workingDayName = "Monday";
        }

        WorkingDay workingDay = workingDayRepository.findByDayNameIgnoreCase(
                workingDayName);

        if (workingDay == null) {
            return new ArrayList<>();
        }

        List<Long> teacherIds = request.getTeacherIds();
        if (teacherIds == null || teacherIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Teacher> teachers = teacherRepository.findAllById(teacherIds);
        if (teachers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Section> uniqueSections = new HashMap<>();

        if (request.getSectionIds() != null) {
            sectionRepository.findAllById(request.getSectionIds())
                    .forEach(section -> uniqueSections.put(section.getId(), section));
        }

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            sectionRepository.findByClassMasterIdIn(request.getClassIds())
                    .forEach(section -> uniqueSections.put(section.getId(), section));
        }

        if (uniqueSections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Section> sections = new ArrayList<>(uniqueSections.values());

        List<Period> periods = periodRepository.findAll();
        if (periods.isEmpty()) {
            return new ArrayList<>();
        }

        periods.sort(Comparator.comparingInt(Period::getPeriodNumber));

        List<TimetableAssignment> generated = new ArrayList<>();
        Map<Long, Set<Long>> occupiedTeacherIdsByPeriod = new HashMap<>();

        for (Section section : sections) {
            Map<Long, Integer> teacherUsageCount = new HashMap<>();
            Set<Long> teacherIdsUsedInSection = new HashSet<>();

            for (int periodIndex = 0; periodIndex < periods.size(); periodIndex++) {
                Period period = periods.get(periodIndex);
                Long periodId = period.getId();
                Set<Long> occupiedTeacherIds = occupiedTeacherIdsByPeriod
                        .computeIfAbsent(periodId, k -> new HashSet<>());

                List<Teacher> candidates = teachers.stream()
                        .filter(teacher -> !occupiedTeacherIds.contains(teacher.getId()))
                        .sorted(Comparator
                                .comparingInt((Teacher teacher) -> teacherIdsUsedInSection.contains(
                                        teacher.getId()) ? 1 : 0)
                                .thenComparingInt(teacher -> teacherUsageCount.getOrDefault(
                                        teacher.getId(), 0))
                                .thenComparingLong(Teacher::getId))
                        .collect(Collectors.toList());

                if (candidates.isEmpty()) {
                    continue;
                }

                int startIndex = periodIndex % candidates.size();
                boolean assigned = false;

                for (int offset = 0; offset < candidates.size(); offset++) {
                    Teacher teacher = candidates.get((startIndex + offset) % candidates.size());

                    TimetableAllocationRequest allocationRequest = new TimetableAllocationRequest();
                    allocationRequest.setTeacherId(teacher.getId());
                    allocationRequest.setSectionId(section.getId());
                    allocationRequest.setWorkingDayId(workingDay.getId());
                    allocationRequest.setPeriodId(periodId);

                    TimetableAllocationResponse response = allocationService.allocate(
                            allocationRequest);

                    if (Boolean.TRUE.equals(response.getSuccess())) {
                        TimetableAssignment assignment = assignmentRepository
                                .findByTeacherAndWorkingDayAndPeriod(
                                        teacher, workingDay, period);

                        if (assignment != null) {
                            generated.add(assignment);
                            occupiedTeacherIds.add(teacher.getId());
                            teacherIdsUsedInSection.add(teacher.getId());
                            teacherUsageCount.merge(teacher.getId(), 1, Integer::sum);
                            assigned = true;
                            break;
                        }
                    }
                }

                if (assigned) {
                    continue;
                }
            }
        }

        return generated;
    }
}
