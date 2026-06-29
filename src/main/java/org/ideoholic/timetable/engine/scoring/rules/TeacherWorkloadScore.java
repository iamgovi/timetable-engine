package org.ideoholic.timetable.engine.scoring.rules;

import org.ideoholic.timetable.engine.planning.models.TeacherUtilization;
import org.ideoholic.timetable.engine.scoring.ScoreCalculator;
import org.ideoholic.timetable.engine.scoring.ScoringContext;
import org.ideoholic.timetable.entity.Teacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeacherWorkloadScore
        implements ScoreCalculator {

    private final int weight;

    private final int currentGenerationUsagePenalty;

    public TeacherWorkloadScore(
            @Value("${timetable.scoring.teacher-utilization-weight:80}")
            int weight,
            @Value("${timetable.scoring.teacher-current-usage-penalty:20}")
            int currentGenerationUsagePenalty) {

        this.weight = weight;
        this.currentGenerationUsagePenalty = currentGenerationUsagePenalty;
    }

    @Override
    public String name() {
        return "TeacherWorkloadScore";
    }

    @Override
    public int calculate(
            ScoringContext context) {

        Teacher teacher = context.getTeacher();
        if (teacher == null || teacher.getId() == null) {
            return 0;
        }

        TeacherUtilization utilization = context.getTeacherUtilizationByTeacherId()
                .get(teacher.getId());
        int score = utilization == null
                ? 0
                : (int) Math.round((100.0 - utilization.getUtilizationPercent()) * weight / 100.0);

        int currentUsage = context.getTeacherUsageCount().getOrDefault(teacher.getId(), 0);
        return score - currentUsage * currentGenerationUsagePenalty;
    }
}
