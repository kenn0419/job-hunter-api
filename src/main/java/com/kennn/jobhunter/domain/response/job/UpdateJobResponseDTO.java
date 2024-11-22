package com.kennn.jobhunter.domain.response.job;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.kennn.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class UpdateJobResponseDTO {
    private long id;

    private String name;

    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private String description;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private Instant updatedAt;

    private String updatedBy;

    private List<String> skills;
}
