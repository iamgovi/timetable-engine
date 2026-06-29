package org.ideoholic.timetable.engine.feasibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ideoholic.timetable.dto.FeasibilityRequest;
import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.entity.AcademicYear;
import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.entity.Curriculum;
import org.ideoholic.timetable.entity.CurriculumSubject;
import org.ideoholic.timetable.entity.Period;
import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.entity.TeacherAvailability;
import org.ideoholic.timetable.entity.TeacherSubjectMapping;
import org.ideoholic.timetable.entity.WorkingDay;

public class FeasibilityContext {

    private FeasibilityRequest request;

    private AcademicYear academicYear;

    private List<ClassMaster> classes = new ArrayList<>();

    private List<Section> sections = new ArrayList<>();

    private List<WorkingDay> workingDays = new ArrayList<>();

    private List<Period> periods = new ArrayList<>();

    private List<Curriculum> curricula = new ArrayList<>();

    private Map<Long, List<CurriculumSubject>> curriculumSubjectsByCurriculumId = new HashMap<>();

    private List<TeacherSubjectMapping> teacherSubjectMappings = new ArrayList<>();

    private List<TeacherAvailability> teacherAvailabilities = new ArrayList<>();

    private AcademicPlan academicPlan;

    public FeasibilityRequest getRequest() {
        return request;
    }

    public void setRequest(FeasibilityRequest request) {
        this.request = request;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public List<ClassMaster> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassMaster> classes) {
        this.classes = classes;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<WorkingDay> workingDays) {
        this.workingDays = workingDays;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public List<Curriculum> getCurricula() {
        return curricula;
    }

    public void setCurricula(List<Curriculum> curricula) {
        this.curricula = curricula;
    }

    public Map<Long, List<CurriculumSubject>> getCurriculumSubjectsByCurriculumId() {
        return curriculumSubjectsByCurriculumId;
    }

    public void setCurriculumSubjectsByCurriculumId(
            Map<Long, List<CurriculumSubject>> curriculumSubjectsByCurriculumId) {
        this.curriculumSubjectsByCurriculumId = curriculumSubjectsByCurriculumId;
    }

    public List<TeacherSubjectMapping> getTeacherSubjectMappings() {
        return teacherSubjectMappings;
    }

    public void setTeacherSubjectMappings(List<TeacherSubjectMapping> teacherSubjectMappings) {
        this.teacherSubjectMappings = teacherSubjectMappings;
    }

    public List<TeacherAvailability> getTeacherAvailabilities() {
        return teacherAvailabilities;
    }

    public void setTeacherAvailabilities(List<TeacherAvailability> teacherAvailabilities) {
        this.teacherAvailabilities = teacherAvailabilities;
    }

    public AcademicPlan getAcademicPlan() {
        return academicPlan;
    }

    public void setAcademicPlan(AcademicPlan academicPlan) {
        this.academicPlan = academicPlan;
    }

    public int availableSlotsPerSection() {
        return Math.max(0, workingDays.size() * periods.size());
    }
}
