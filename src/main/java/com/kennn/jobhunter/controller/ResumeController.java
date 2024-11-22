package com.kennn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;

import jakarta.validation.Valid;
import com.kennn.jobhunter.domain.Company;
import com.kennn.jobhunter.domain.Job;
import com.kennn.jobhunter.domain.Resume;
import com.kennn.jobhunter.domain.User;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.resume.CreateResumeResponseDTO;
import com.kennn.jobhunter.domain.response.resume.ResumeResponseDTO;
import com.kennn.jobhunter.domain.response.resume.UpdateResumeResponseDTO;
import com.kennn.jobhunter.service.ResumeService;
import com.kennn.jobhunter.service.UserService;
import com.kennn.jobhunter.util.SecurityUtil;
import com.kennn.jobhunter.util.annotation.APIMessage;
import com.kennn.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @APIMessage("Create resume successfully")
    public ResponseEntity<CreateResumeResponseDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        if (!this.resumeService.checkExistUserAndJob(resume)) {
            throw new IdInvalidException("User/Job không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @APIMessage("Update resume successfully")
    public ResponseEntity<UpdateResumeResponseDTO> updateResume(@RequestBody Resume resume)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.resumeService.update(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @APIMessage("Delete resume successfully")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        this.resumeService.remove(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @APIMessage("Get resume by id")
    public ResponseEntity<ResumeResponseDTO> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Resume existResume = this.resumeService.fetchResumeById(id);
        if (existResume == null) {
            throw new IdInvalidException("Không tồn tại resume với id: " + id);
        }
        return ResponseEntity.ok()
                .body(this.resumeService.convertToResumeResponseDTO(existResume));
    }

    @GetMapping("/resumes")
    @APIMessage("Get resumes list")
    public ResponseEntity<ResultPaginationDTO> getResumesList(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.getUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(job -> job.getId()).collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());
        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok().body(this.resumeService.fetchAllResumes(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
