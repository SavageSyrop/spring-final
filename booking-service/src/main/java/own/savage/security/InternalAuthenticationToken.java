package own.savage.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class InternalAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    @Getter
    private final InternalAuthData authData;

    public InternalAuthenticationToken(String username,
                                       Collection<? extends GrantedAuthority> authorities,
                                       InternalAuthData authData) {
        super(authorities);
        this.username = username;
        this.authData = authData;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return authData.getRoles();
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
