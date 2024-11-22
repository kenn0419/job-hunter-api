package com.kennn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.kennn.jobhunter.domain.Job;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.job.CreateJobResponseDTO;
import com.kennn.jobhunter.domain.response.job.UpdateJobResponseDTO;
import com.kennn.jobhunter.service.JobService;
import com.kennn.jobhunter.util.annotation.APIMessage;
import com.kennn.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<CreateJobResponseDTO> createJob(@RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.save(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getJobs(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fetchAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.jobService.fetchJobById(id));
    }

    @PutMapping("/jobs")
    public ResponseEntity<UpdateJobResponseDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        return ResponseEntity.ok().body(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @APIMessage("Delete job successfully")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) {
        this.jobService.remove(id);
        return ResponseEntity.ok().body(null);
    }

}
