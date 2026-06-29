package org.ideoholic.timetable.engine.strategy.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.Subject;
import org.ideoholic.timetable.entity.Teacher;
import org.ideoholic.timetable.entity.WorkingDay;

public class GenerationContext {

    private Section section;

    private WorkingDay workingDay;

    private Period period;

    private int periodsInDay;

    private List<Subject> availableSubjects;

    private Map<Long, List<Teacher>> teachersBySubjectId;

    private Set<Long> occupiedTeacherIds;

    private Map<Long, Integer> weeklySubjectCount;

    private Map<Long, Integer> daySubjectCount;

    private Map<Long, Integer> samePeriodSubjectCount;

    private Long previousPeriodSubjectId;

    private Long previousDayPeriodSubjectId;

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public WorkingDay getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(WorkingDay workingDay) {
        this.workingDay = workingDay;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public int getPeriodsInDay() {
        return periodsInDay;
    }

    public void setPeriodsInDay(int periodsInDay) {
        this.periodsInDay = periodsInDay;
    }

    public List<Subject> getAvailableSubjects() {
        return availableSubjects;
    }

    public void setAvailableSubjects(List<Subject> availableSubjects) {
        this.availableSubjects = availableSubjects;
    }

    public Map<Long, List<Teacher>> getTeachersBySubjectId() {
        return teachersBySubjectId;
    }

    public void setTeachersBySubjectId(Map<Long, List<Teacher>> teachersBySubjectId) {
        this.teachersBySubjectId = teachersBySubjectId;
    }

    public Set<Long> getOccupiedTeacherIds() {
        return occupiedTeacherIds;
    }

    public void setOccupiedTeacherIds(Set<Long> occupiedTeacherIds) {
        this.occupiedTeacherIds = occupiedTeacherIds;
    }

    public Map<Long, Integer> getWeeklySubjectCount() {
        return weeklySubjectCount;
    }

    public void setWeeklySubjectCount(Map<Long, Integer> weeklySubjectCount) {
        this.weeklySubjectCount = weeklySubjectCount;
    }

    public Map<Long, Integer> getDaySubjectCount() {
        return daySubjectCount;
    }

    public void setDaySubjectCount(Map<Long, Integer> daySubjectCount) {
        this.daySubjectCount = daySubjectCount;
    }

    public Map<Long, Integer> getSamePeriodSubjectCount() {
        return samePeriodSubjectCount;
    }

    public void setSamePeriodSubjectCount(Map<Long, Integer> samePeriodSubjectCount) {
        this.samePeriodSubjectCount = samePeriodSubjectCount;
    }

    public Long getPreviousPeriodSubjectId() {
        return previousPeriodSubjectId;
    }

    public void setPreviousPeriodSubjectId(Long previousPeriodSubjectId) {
        this.previousPeriodSubjectId = previousPeriodSubjectId;
    }

    public Long getPreviousDayPeriodSubjectId() {
        return previousDayPeriodSubjectId;
    }

    public void setPreviousDayPeriodSubjectId(Long previousDayPeriodSubjectId) {
        this.previousDayPeriodSubjectId = previousDayPeriodSubjectId;
    }
}
