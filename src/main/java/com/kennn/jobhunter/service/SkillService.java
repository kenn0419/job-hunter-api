package com.kennn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Skill;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.repository.SkillRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill save(Skill skill) throws IdInvalidException {
        boolean existSkill = this.skillRepository.existsByName(skill.getName());
        if (existSkill) {
            throw new IdInvalidException("Skill này đã tồn tại");
        }
        Skill createSkill = new Skill();
        createSkill.setName(skill.getName());
        return this.skillRepository.save(createSkill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageSkill.getContent());
        return res;
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> optionalSkill = this.skillRepository.findById(id);
        if (optionalSkill.isPresent()) {
            return optionalSkill.get();
        }
        return null;
    }

    public Skill updateSkill(Skill skill) throws IdInvalidException {
        Skill updateSkill = this.fetchSkillById(skill.getId());
        if (updateSkill != null) {
            boolean existSkill = this.skillRepository.existsByName(skill.getName());
            if (existSkill) {
                throw new IdInvalidException("Skill này đã tồn tại");
            }
            updateSkill.setName(skill.getName());
            return this.skillRepository.save(updateSkill);
        }
        throw new IdInvalidException("Skill này không tồn tại");
    }

    public void remove(long id) {
        Skill skill = this.fetchSkillById(id);
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));

        this.skillRepository.delete(skill);
    }
}
