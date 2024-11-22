package com.kennn.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kennn.jobhunter.domain.Job;
import com.kennn.jobhunter.domain.Resume;
import com.kennn.jobhunter.domain.User;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {
    boolean existsByUserAndJob(User user, Job job);
}
