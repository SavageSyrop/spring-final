package own.savage.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class InternalAuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;
    @Getter
    private final InternalAuthData authData;

    public InternalAuthenticationToken(String userId,
                                       Collection<? extends GrantedAuthority> authorities,
                                       InternalAuthData authData) {
        super(authorities);
        this.userId = userId;
        this.authData = authData;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
