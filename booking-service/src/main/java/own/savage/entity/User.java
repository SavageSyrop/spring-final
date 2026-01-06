package own.savage.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User extends AbstractEntity {
    private String username;
    private String passwordHash;
    private Role role;
}


