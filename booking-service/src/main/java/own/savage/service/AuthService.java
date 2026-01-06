package own.savage.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import own.savage.dao.UserDAO;
import own.savage.entity.Role;
import own.savage.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {
    private final UserDAO userDAO;
    private final SecretKey key;

    public AuthService(UserDAO userDAO, @Value("${jwt.secret}") String secret) {
        this.userDAO = userDAO;
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            bytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public User register(String username, String password, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        u.setRole(role);
        return userDAO.save(u);
    }

    public String login(String username, String password) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Bad credentials");
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(Map.of(
                        "roles", user.getRole()
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(3600000)))
                .signWith(key)
                .compact();
    }
}


