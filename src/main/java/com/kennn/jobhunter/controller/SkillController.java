package com.kennn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.kennn.jobhunter.domain.Skill;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.service.SkillService;
import com.kennn.jobhunter.util.annotation.APIMessage;
import com.kennn.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.save(skill));
    }

    @GetMapping("/skills")
    @APIMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.fetchAllSkills(spec, pageable));
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.skillService.fetchSkillById(id));
    }

    @PutMapping("/skills")
    @APIMessage("Cập nhật thành công")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        return ResponseEntity.ok().body(this.skillService.updateSkill(skill));
    }

    @DeleteMapping("/skills/{id}")
    @APIMessage("Xoá skill thành công")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) {
        this.skillService.remove(id);
        return ResponseEntity.ok().body(null);
    }
}
