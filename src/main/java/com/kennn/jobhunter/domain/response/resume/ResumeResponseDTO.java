package com.kennn.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.kennn.jobhunter.util.constant.ResumeStatusEnum;

@Getter
@Setter
public class ResumeResponseDTO {
    private long id;

    private String email;

    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStatusEnum status;

    private User user;

    private Job job;

    private String companyName;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Job {
        private long id;
        private String name;
    }
}
