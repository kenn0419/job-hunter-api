package com.kennn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import com.kennn.jobhunter.domain.Resume;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.resume.CreateResumeResponseDTO;
import com.kennn.jobhunter.domain.response.resume.ResumeResponseDTO;
import com.kennn.jobhunter.domain.response.resume.UpdateResumeResponseDTO;
import com.kennn.jobhunter.repository.ResumeRepository;
import com.kennn.jobhunter.util.SecurityUtil;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService,
            FilterParser filterParser, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public CreateResumeResponseDTO create(Resume resume) {
        Resume newResume = new Resume();
        newResume.setEmail(resume.getEmail());
        newResume.setUrl(resume.getUrl());
        newResume.setStatus(resume.getStatus());
        newResume.setUser(resume.getUser());
        newResume.setJob(resume.getJob());

        return this.convertToCreateResumeResponseDTO(this.resumeRepository.save(newResume));
    }

    public Resume fetchResumeById(long id) {
        Optional<Resume> optionalResume = this.resumeRepository.findById(id);
        if (optionalResume.isPresent()) {
            return optionalResume.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta resMeta = new ResultPaginationDTO.Meta();
        resMeta.setPage(pageable.getPageNumber() + 1);
        resMeta.setPageSize(pageable.getPageSize());
        resMeta.setPages(pageResume.getTotalPages());
        resMeta.setTotal(pageResume.getTotalElements());

        res.setMeta(resMeta);

        List<ResumeResponseDTO> resumes = pageResume.getContent().stream()
                .map(resume -> this.convertToResumeResponseDTO(resume)).collect(Collectors.toList());
        res.setResult(resumes);

        return res;
    }

    public UpdateResumeResponseDTO update(Resume resume) throws IdInvalidException {
        Resume updateResume = this.fetchResumeById(resume.getId());
        if (updateResume != null) {
            updateResume.setStatus(resume.getStatus());
            return this.convertToUpdateResumeResponseDTO(this.resumeRepository.save(updateResume));
        }
        throw new IdInvalidException("Không tồn tại resume với id: " + resume.getId());
    }

    public void remove(long id) throws IdInvalidException {
        Resume deleteResume = this.fetchResumeById(id);
        if (deleteResume != null) {
            this.resumeRepository.delete(deleteResume);
        }
        throw new IdInvalidException("Không tồn tại resume với id: " + id);
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta resMeta = new ResultPaginationDTO.Meta();
        resMeta.setPage(pageable.getPageNumber() + 1);
        resMeta.setPageSize(pageable.getPageSize());
        resMeta.setPages(pageResume.getTotalPages());
        resMeta.setTotal(pageResume.getTotalElements());

        res.setMeta(resMeta);
        List<ResumeResponseDTO> resumes = pageResume.getContent().stream()
                .map(resume -> this.convertToResumeResponseDTO(resume)).collect(Collectors.toList());
        res.setResult(resumes);
        return res;
    }

    public boolean checkExistUserAndJob(Resume resume) {
        if (resume.getUser() == null) {
            return false;
        }
        if (this.userService.fetchUserById(resume.getUser().getId()) == null) {
            return false;
        }

        if (resume.getJob() == null) {
            return false;
        }
        if (this.jobService.fetchJobById(resume.getJob().getId()) == null) {
            return false;
        }

        return true;
    }

    public CreateResumeResponseDTO convertToCreateResumeResponseDTO(Resume resume) {
        CreateResumeResponseDTO res = new CreateResumeResponseDTO();

        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public UpdateResumeResponseDTO convertToUpdateResumeResponseDTO(Resume resume) {
        UpdateResumeResponseDTO res = new UpdateResumeResponseDTO();

        res.setId(resume.getId());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        return res;
    }

    public ResumeResponseDTO convertToResumeResponseDTO(Resume resume) {
        ResumeResponseDTO res = new ResumeResponseDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setStatus(resume.getStatus());
        res.setUrl(resume.getUrl());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        ResumeResponseDTO.User userResume = new ResumeResponseDTO.User(resume.getUser().getId(),
                resume.getUser().getName());
        res.setUser(userResume);
        ResumeResponseDTO.Job jobResume = new ResumeResponseDTO.Job(resume.getJob().getId(),
                resume.getJob().getName());
        res.setJob(jobResume);
        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        return res;
    }
}
