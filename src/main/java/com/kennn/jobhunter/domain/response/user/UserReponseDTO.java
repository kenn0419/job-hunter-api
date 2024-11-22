package com.kennn.jobhunter.domain.response.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.kennn.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReponseDTO {
    private long id;

    private String name;

    private String email;

    private int age;

    private GenderEnum gender;

    private String address;

    private Instant createdAt;

    private Instant updatedAt;

    private Company company;

    private Role role;

    @Getter
    @Setter
    public static class Company {
        private long id;

        private String name;
    }

    @Getter
    @Setter
    public static class Role {
        private long id;

        private String name;
    }
}
