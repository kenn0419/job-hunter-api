package com.kennn.jobhunter.domain.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import com.kennn.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class UpdateUserReponseDTO {
    private long id;

    private String name;

    private String email;

    private int age;

    private GenderEnum gender;

    private String address;

    private Instant updatedAt;

    private Company company;

    @Getter
    @Setter
    public static class Company {
        private long id;

        private String name;
    }
}
