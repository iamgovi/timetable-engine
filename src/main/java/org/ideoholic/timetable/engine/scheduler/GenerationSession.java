package org.ideoholic.timetable.engine.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class GenerationSession {

    private final String sessionId = UUID.randomUUID().toString();

    private Instant startTime;

    private Instant finishTime;

    private GenerationStatus status = GenerationStatus.PENDING;

    private int completedSections;

    private int failedSections;

    private int assignmentsGenerated;

    private long executionDurationMillis;

    public void start() {
        startTime = Instant.now();
        status = GenerationStatus.RUNNING;
    }

    public void recordSuccess(
            int assignmentCount) {

        completedSections++;
        assignmentsGenerated += Math.max(0, assignmentCount);
    }

    public void recordFailure() {
        failedSections++;
    }

    public void finish() {
        finishTime = Instant.now();
        if (startTime != null) {
            executionDurationMillis = Duration.between(startTime, finishTime).toMillis();
        }
        status = failedSections > 0
                ? GenerationStatus.COMPLETED_WITH_FAILURES
                : GenerationStatus.COMPLETED;
    }

    public void fail() {
        finishTime = Instant.now();
        if (startTime != null) {
            executionDurationMillis = Duration.between(startTime, finishTime).toMillis();
        }
        status = GenerationStatus.FAILED;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public GenerationStatus getStatus() {
        return status;
    }

    public int getCompletedSections() {
        return completedSections;
    }

    public int getFailedSections() {
        return failedSections;
    }

    public int getAssignmentsGenerated() {
        return assignmentsGenerated;
    }

    public long getExecutionDurationMillis() {
        return executionDurationMillis;
    }
}
