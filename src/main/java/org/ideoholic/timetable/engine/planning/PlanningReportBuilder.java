package org.ideoholic.timetable.engine.planning;

import java.util.List;

import org.ideoholic.timetable.engine.planning.models.AcademicPlan;
import org.ideoholic.timetable.engine.planning.models.ClassDemand;
import org.ideoholic.timetable.engine.planning.models.CurriculumDemandResult;
import org.ideoholic.timetable.engine.planning.models.PlanningSummary;
import org.ideoholic.timetable.engine.planning.models.TeacherRequirement;
import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.springframework.stereotype.Component;

@Component
public class PlanningReportBuilder {

    public AcademicPlan build(
            CurriculumDemandResult demandResult,
            List<TeacherRequirement> teacherRequirements,
            List<TeacherUtilization> teacherUtilizations) {

        AcademicPlan plan = new AcademicPlan();
        plan.setClassDemands(demandResult.getClassDemands());
        plan.setSubjectDemands(demandResult.getSubjectDemands());
        plan.setTeacherRequirements(teacherRequirements);
        plan.setTeacherUtilizations(teacherUtilizations);
        plan.setSummary(summary(demandResult, teacherRequirements, teacherUtilizations));
        return plan;
    }

    private PlanningSummary summary(
            CurriculumDemandResult demandResult,
            List<TeacherRequirement> teacherRequirements,
            List<TeacherUtilization> teacherUtilizations) {

        PlanningSummary summary = new PlanningSummary();
        summary.setTotalClasses(demandResult.getClassDemands().size());
        summary.setTotalSections(demandResult.getClassDemands().stream()
                .mapToInt(ClassDemand::getSectionCount)
                .sum());
        summary.setTotalCurriculumPeriods(demandResult.getClassDemands().stream()
                .mapToInt(ClassDemand::getTotalWeeklyPeriods)
                .sum());
        summary.setTotalTeachers(teacherUtilizations.size());
        summary.setSubjectsWithShortages((int) teacherRequirements.stream()
                .filter(requirement -> "SHORTAGE".equals(requirement.getStatus()))
                .count());
        summary.setTeachersOverCapacity((int) teacherUtilizations.stream()
                .filter(TeacherUtilization::isOverCapacity)
                .count());
        summary.setTeachersUnderUtilized((int) teacherUtilizations.stream()
                .filter(TeacherUtilization::isUnderUtilized)
                .count());
        summary.setGenerationFeasible(summary.getSubjectsWithShortages() == 0
                && summary.getTeachersOverCapacity() == 0
                && summary.getTotalClasses() > 0
                && summary.getTotalSections() > 0
                && summary.getTotalCurriculumPeriods() > 0);
        return summary;
    }
}
