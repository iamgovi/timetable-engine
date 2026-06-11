package org.ideoholic.timetable.entity;

import java.time.LocalTime;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "period")
@Data
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_number")
    private Integer periodNumber;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_break")
    private Boolean breakPeriod;

    public Long getId() {
        return id;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }
}
