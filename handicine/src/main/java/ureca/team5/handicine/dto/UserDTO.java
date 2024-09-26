package ureca.team5.handicine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    @JsonProperty("role_name")
    private String roleName;
    private String password;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, String roleName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
    }

    public UserDTO(Long id, String username, String email, String roleName, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
