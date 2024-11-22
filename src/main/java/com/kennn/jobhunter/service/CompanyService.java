package com.kennn.jobhunter.service;

import java.util.*;
import com.kennn.jobhunter.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Company;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.repository.CompanyRepository;
import com.kennn.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public CompanyService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public Company create(Company currentCompany) {
        Company company = new Company();

        company.setName(currentCompany.getName());
        company.setDescription(currentCompany.getDescription());
        company.setAddress(currentCompany.getAddress());
        company.setLogo(currentCompany.getLogo());

        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageCompany.getContent());
        return result;
    }

    public Company fetchById(long id) {
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            return optionalCompany.get();
        }
        return null;
    }

    public void removeById(long id) {
        Company company = this.fetchById(id);
        if (company != null) {
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Company update(Company currentCompany) {
        Company company = this.fetchById(currentCompany.getId());
        if (company != null) {
            company.setName(currentCompany.getName());
            company.setDescription(currentCompany.getDescription());
            company.setAddress(currentCompany.getAddress());
            company.setLogo(currentCompany.getLogo());

            this.companyRepository.save(company);
        }
        return company;
    }
}
