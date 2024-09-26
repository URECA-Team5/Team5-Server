package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.RoleDTO;
import ureca.team5.handicine.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 모든 role 조회
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // 새로운 role 추가
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return ResponseEntity.ok(createdRole);
    }

    // 특정 role 수정
    @PatchMapping("/{role_id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable("role_id") Long role_id, @RequestBody RoleDTO roleDTO) {
        RoleDTO updatedRole = roleService.updateRole(role_id, roleDTO);
        return ResponseEntity.ok(updatedRole);
    }

    // 특정 role 삭제
    @DeleteMapping("/{role_id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("role_id") Long role_id) {
        roleService.deleteRole(role_id);
        return ResponseEntity.noContent().build();
    }
}
