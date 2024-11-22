package com.kennn.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateResumeResponseDTO {
    private long id;

    private Instant updatedAt;

    private String updatedBy;
}
