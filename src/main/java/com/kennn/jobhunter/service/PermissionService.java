package com.kennn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Permission;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.repository.PermissionRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission create(Permission permission) throws IdInvalidException {
        boolean existsPermission = this.existsByApiPathAndMethodAndModule(permission.getApiPath(),
                permission.getMethod(), permission.getModule());
        if (existsPermission) {
            throw new IdInvalidException("Permission này đã tồn tại");
        }
        Permission newPermission = new Permission();
        newPermission.setName(permission.getName());
        newPermission.setApiPath(permission.getApiPath());
        newPermission.setMethod(permission.getMethod());
        newPermission.setModule(permission.getModule());

        return this.permissionRepository.save(newPermission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            return optionalPermission.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermisson = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta resMeta = new ResultPaginationDTO.Meta();

        resMeta.setPage(pageable.getPageNumber() + 1);
        resMeta.setPageSize(pageable.getPageSize());
        resMeta.setPages(pagePermisson.getTotalPages());
        resMeta.setTotal(pagePermisson.getTotalElements());

        res.setMeta(resMeta);
        res.setResult(pagePermisson.getContent());

        return res;
    }

    public Permission update(Permission permission) throws IdInvalidException {
        Permission updatePermission = this.fetchPermissionById(permission.getId());
        if (updatePermission != null) {
            boolean existsPermission = this.existsByApiPathAndMethodAndModule(permission.getApiPath(),
                    permission.getMethod(), permission.getModule());
            if (existsPermission) {
                if (updatePermission.getName().equals(permission.getName())) {
                    throw new IdInvalidException("Permission này đã tồn tại");
                }
            }
            updatePermission.setName(permission.getName());
            updatePermission.setApiPath(permission.getApiPath());
            updatePermission.setMethod(permission.getMethod());
            updatePermission.setModule(permission.getModule());

            return this.permissionRepository.save(updatePermission);
        }
        throw new IdInvalidException("Không tồn tại permission với id: " + permission.getId());
    }

    public boolean existsByApiPathAndMethodAndModule(String apiPath, String method, String module) {
        return this.permissionRepository.existsByApiPathAndMethodAndModule(apiPath, method, module);
    }
}
