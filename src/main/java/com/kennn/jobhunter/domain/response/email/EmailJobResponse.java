package com.kennn.jobhunter.domain.response.email;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailJobResponse {
    private String name;

    private double salary;

    private Company company;

    private List<Skill> skills;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Company {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Skill {
        private String name;
    }
}
