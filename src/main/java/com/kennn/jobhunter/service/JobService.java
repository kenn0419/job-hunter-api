package com.kennn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Company;
import com.kennn.jobhunter.domain.Job;
import com.kennn.jobhunter.domain.Skill;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.job.CreateJobResponseDTO;
import com.kennn.jobhunter.domain.response.job.UpdateJobResponseDTO;
import com.kennn.jobhunter.repository.CompanyRepository;
import com.kennn.jobhunter.repository.JobRepository;
import com.kennn.jobhunter.repository.SkillRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public CreateJobResponseDTO save(Job job) {
        Job newJob = new Job();
        if (job.getSkills() != null) {
            List<Skill> existsSkills = this.skillRepository
                    .findByIdIn(job.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList()));
            newJob.setSkills(existsSkills);
        }
        if (job.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyRepository.findById(job.getCompany().getId());
            if (optionalCompany.isPresent()) {
                newJob.setCompany(optionalCompany.get());
            }
        }
        newJob.setName(job.getName());
        newJob.setLocation(job.getLocation());
        newJob.setDescription(job.getDescription());
        newJob.setSalary(job.getSalary());
        newJob.setQuantity(job.getQuantity());
        newJob.setLevel(job.getLevel());
        newJob.setStartDate(job.getStartDate());
        newJob.setEndDate(job.getEndDate());
        newJob.setActive(job.isActive());

        return convertToCreateJobResponseDTO(this.jobRepository.save(newJob));
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageJob.getContent());

        return res;
    }

    public Job fetchJobById(long id) {
        Optional<Job> optionalJob = this.jobRepository.findById(id);
        if (optionalJob.isPresent()) {
            return optionalJob.get();
        }
        return null;
    }

    public UpdateJobResponseDTO update(Job job) throws IdInvalidException {
        Job updateJob = this.fetchJobById(job.getId());
        if (updateJob != null) {
            if (job.getSkills() != null) {
                List<Skill> existsSkills = this.skillRepository
                        .findByIdIn(job.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList()));
                updateJob.setSkills(existsSkills);
            }
            if (job.getCompany() != null) {
                Optional<Company> optionalCompany = this.companyRepository.findById(job.getCompany().getId());
                if (optionalCompany.isPresent()) {
                    updateJob.setCompany(optionalCompany.get());
                }
            }
            updateJob.setName(job.getName());
            updateJob.setLocation(job.getLocation());
            updateJob.setDescription(job.getDescription());
            updateJob.setSalary(job.getSalary());
            updateJob.setQuantity(job.getQuantity());
            updateJob.setLevel(job.getLevel());
            updateJob.setStartDate(job.getStartDate());
            updateJob.setEndDate(job.getEndDate());
            updateJob.setActive(job.isActive());

            return this.convertToUpdateJobResponseDTO(this.jobRepository.save(updateJob));
        }
        throw new IdInvalidException("Job này không tồn tại");
    }

    public void remove(long id) {
        this.jobRepository.deleteById(id);
    }

    public CreateJobResponseDTO convertToCreateJobResponseDTO(Job job) {
        CreateJobResponseDTO res = new CreateJobResponseDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setDescription(job.getDescription());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLevel(job.getLevel());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());
        res.setSkills(job.getSkills().stream().map(skill -> skill.getName()).collect(Collectors.toList()));
        res.setCreatedAt(job.getCreatedAt());
        res.setCreatedBy(job.getCreatedBy());
        return res;
    }

    public UpdateJobResponseDTO convertToUpdateJobResponseDTO(Job job) {
        UpdateJobResponseDTO res = new UpdateJobResponseDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setDescription(job.getDescription());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLevel(job.getLevel());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());
        res.setSkills(job.getSkills().stream().map(skill -> skill.getName()).collect(Collectors.toList()));
        res.setUpdatedAt(job.getUpdatedAt());
        res.setUpdatedBy(job.getUpdatedBy());
        return res;
    }
}
