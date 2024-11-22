package com.kennn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Job;
import com.kennn.jobhunter.domain.Skill;
import com.kennn.jobhunter.domain.Subscriber;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.email.EmailJobResponse;
import com.kennn.jobhunter.repository.JobRepository;
import com.kennn.jobhunter.repository.SkillRepository;
import com.kennn.jobhunter.repository.SubscriberRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class SubscriberService {
    private final EmailService emailService;
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public SubscriberService(EmailService emailService, SubscriberRepository subscriberRepository,
            SkillRepository skillRepository, JobRepository jobRepository) {
        this.emailService = emailService;
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public Subscriber create(Subscriber subscriber) throws IdInvalidException {
        Subscriber newSubscriber = new Subscriber();

        boolean existSubscriber = this.subscriberRepository.existsByEmail(subscriber.getEmail());
        if (existSubscriber) {
            throw new IdInvalidException("Email này đã được subscriber");
        }

        newSubscriber.setEmail(subscriber.getEmail());
        newSubscriber.setName(subscriber.getName());
        if (subscriber.getSkills() != null) {
            List<Skill> existedSkill = this.findExistSkills(subscriber.getSkills());
            newSubscriber.setSkills(existedSkill);
        }

        return this.subscriberRepository.save(newSubscriber);
    }

    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> optionalSubscriber = this.subscriberRepository.findById(id);
        if (optionalSubscriber.isPresent()) {
            return optionalSubscriber.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllSubscribers(Specification<Subscriber> spec, Pageable pageable) {
        Page<Subscriber> pageSubscriber = this.subscriberRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSubscriber.getTotalPages());
        meta.setTotal(pageSubscriber.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageSubscriber.getContent());
        return res;
    }

    public Subscriber update(Subscriber subscriber) throws IdInvalidException {
        Subscriber updateSubscriber = this.fetchSubscriberById(subscriber.getId());
        if (updateSubscriber == null) {
            throw new IdInvalidException("Không tồn tại subscriber với id: " + subscriber.getId());
        }
        if (subscriber.getSkills() != null) {
            List<Skill> existedSkill = this.findExistSkills(subscriber.getSkills());
            updateSubscriber.setSkills(existedSkill);
        }

        return this.subscriberRepository.save(updateSubscriber);
    }

    public void remove(long id) throws IdInvalidException {
        Subscriber deleteSubscriber = this.fetchSubscriberById(id);
        if (deleteSubscriber == null) {
            throw new IdInvalidException("Không tồn tại subscriber với id: " + id);
        }
        this.subscriberRepository.delete(deleteSubscriber);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<EmailJobResponse> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public List<Skill> findExistSkills(List<Skill> skills) {
        return this.skillRepository
                .findByIdIn(skills.stream().map(skill -> skill.getId()).collect(Collectors.toList()));
    }

    public EmailJobResponse convertJobToSendEmail(Job job) {
        EmailJobResponse res = new EmailJobResponse();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new EmailJobResponse.Company(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<EmailJobResponse.Skill> s = skills.stream().map(skill -> new EmailJobResponse.Skill(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }
}
