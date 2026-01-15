package own.savage.dto;

import lombok.Getter;
import lombok.Setter;
import own.savage.entity.Role;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Role role;
}
