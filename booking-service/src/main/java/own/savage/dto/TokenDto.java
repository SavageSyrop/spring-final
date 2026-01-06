package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private String accessToken;
    private String tokenType;

    public TokenDto(String token, String tokenType) {
        this.accessToken = token;
        this.tokenType = tokenType;
    }
}
