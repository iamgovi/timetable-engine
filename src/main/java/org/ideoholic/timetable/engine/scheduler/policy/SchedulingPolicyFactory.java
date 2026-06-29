package org.ideoholic.timetable.engine.scheduler.policy;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SchedulingPolicyFactory {

    private final List<SchedulingPolicy> policies;

    private final String configuredPolicy;

    public SchedulingPolicyFactory(
            List<SchedulingPolicy> policies,
            @Value("${timetable.scheduler.policy:balanced}") String configuredPolicy) {

        this.policies = policies;
        this.configuredPolicy = configuredPolicy;
    }

    public SchedulingPolicy activePolicy() {
        String policyName = configuredPolicy == null || configuredPolicy.isBlank()
                ? "balanced"
                : configuredPolicy.trim();

        return policies.stream()
                .filter(policy -> policy.name().equalsIgnoreCase(policyName))
                .findFirst()
                .orElseGet(this::balancedPolicy);
    }

    private SchedulingPolicy balancedPolicy() {
        return policies.stream()
                .filter(policy -> "balanced".equalsIgnoreCase(policy.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Balanced scheduling policy is not available"));
    }
}
