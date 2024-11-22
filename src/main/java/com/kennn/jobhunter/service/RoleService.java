package com.kennn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.response.ResultPaginationDTO;

import com.kennn.jobhunter.domain.Permission;
import com.kennn.jobhunter.domain.Role;
import com.kennn.jobhunter.repository.PermissionRepository;
import com.kennn.jobhunter.repository.RoleRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role create(Role role) throws IdInvalidException {
        boolean existRole = this.roleRepository.existsByName(role.getName());
        if (existRole) {
            throw new IdInvalidException("Role với name: " + role.getName() + " đã tồn tại");
        }
        Role newRole = new Role();
        newRole.setName(role.getName());
        newRole.setActive(role.isActive());
        ;
        newRole.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            newRole.setPermissions(this.existPermissionList(role.getPermissions()));
        }

        return this.roleRepository.save(newRole);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> optionalRole = this.roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        res.setMeta(meta);
        res.setResult(pageRole.getContent());
        return res;
    }

    public Role update(Role role) throws IdInvalidException {
        Role updateRole = this.fetchRoleById(role.getId());
        if (updateRole == null) {
            throw new IdInvalidException("Không tồn tại role với id: " + role.getId());
        }
        if (this.roleRepository.existsByName(role.getName()) && !updateRole.getName().equals(role.getName())) {
            throw new IdInvalidException("Role với name: " + role.getName() + " đã tồn tại");
        }

        updateRole.setName(role.getName());
        updateRole.setActive(role.isActive());
        ;
        updateRole.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            updateRole.setPermissions(this.existPermissionList(role.getPermissions()));
        }

        return this.roleRepository.save(updateRole);
    }

    public void remove(long id) throws IdInvalidException {
        Role deleteRole = this.fetchRoleById(id);
        if (deleteRole == null) {
            throw new IdInvalidException("Không tồn tại role với id: " + id);
        }
        this.roleRepository.delete(deleteRole);
    }

    public List<Permission> existPermissionList(List<Permission> permissions) {
        return this.permissionRepository
                .findByIdIn(permissions.stream().map(item -> item.getId()).collect(Collectors.toList()));
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }
}
