package org.ideoholic.timetable.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "working_day")
@Data
public class WorkingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_name",
            nullable = false,
            unique = true)
    private String dayName;

    @Column(name = "is_working")
    private Boolean working;

    public Long getId() {
        return id;
    }
}
