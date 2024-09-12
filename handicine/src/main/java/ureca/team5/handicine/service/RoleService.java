package ureca.team5.handicine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ureca.team5.handicine.dto.RoleDTO;
import ureca.team5.handicine.entity.Role;
import ureca.team5.handicine.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        Role savedRole = roleRepository.save(role);
        return new RoleDTO(savedRole.getRoleId(), savedRole.getRoleName());
    }

    @Transactional
    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found."));
        role.setRoleName(roleDTO.getRoleName());
        Role updatedRole = roleRepository.save(role);
        return new RoleDTO(updatedRole.getRoleId(), updatedRole.getRoleName());
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found."));
        roleRepository.delete(role);
    }
}