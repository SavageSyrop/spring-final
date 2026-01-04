package own.savage.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalAuthData {
    private String userId;
    private String email;
    private List<String> roles;
    private List<String> permissions;
}