package own.savage.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.tokenDuration}")
    private long jwtExpirationMs;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private SecretKey getSigningKey() {
        // Для HS256 нужен ключ минимум 256 бит (32 байта)
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Дополняем ключ до 32 байт если нужно
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return generateToken(claims, username);
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtIssuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateToken(InternalAuthData userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUserId());
        claims.put("email", userDetails.getEmail());
        claims.put("roles", userDetails.getRoles());
        claims.put("permissions", userDetails.getPermissions());

        return generateToken(claims, userDetails.getEmail());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = parseClaims(token);

        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        if (authorities == null) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", String.class);
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = parseClaims(token);
        return new HashMap<>(claims);
    }

    public String extractTokenFromHeader(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}