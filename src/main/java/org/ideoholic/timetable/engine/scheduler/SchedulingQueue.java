package org.ideoholic.timetable.engine.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ordered queue of section scheduling tasks.
 */
public class SchedulingQueue {

    private final List<SchedulingTask> tasks = new ArrayList<>();

    public SchedulingQueue() {
    }

    public SchedulingQueue(
            List<SchedulingTask> tasks) {

        if (tasks != null) {
            this.tasks.addAll(tasks);
        }
    }

    public void add(
            SchedulingTask task) {

        if (task != null) {
            tasks.add(task);
        }
    }

    public void order() {
        tasks.sort(Comparator
                .comparingInt(SchedulingTask::getPriorityScore)
                .reversed()
                .thenComparing(task -> task.getClassMaster() == null
                        || task.getClassMaster().getId() == null
                                ? Long.MAX_VALUE
                                : task.getClassMaster().getId())
                .thenComparing(task -> task.getSection() == null
                        || task.getSection().getSectionName() == null
                                ? ""
                                : task.getSection().getSectionName())
                .thenComparing(task -> task.getSection() == null
                        || task.getSection().getId() == null
                                ? Long.MAX_VALUE
                                : task.getSection().getId()));
    }

    public Optional<SchedulingTask> next() {
        if (tasks.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tasks.remove(0));
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public int size() {
        return tasks.size();
    }

    public List<SchedulingTask> tasks() {
        return new ArrayList<>(tasks);
    }
}
