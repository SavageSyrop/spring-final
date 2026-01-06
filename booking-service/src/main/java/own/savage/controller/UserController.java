package own.savage.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import own.savage.dto.TokenDto;
import own.savage.dto.UserDto;
import own.savage.entity.User;
import own.savage.service.AuthService;
import own.savage.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    public UserController(@Autowired UserService userService, @Autowired AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> list() {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : userService.findAll()) {
            dtos.add(convertToDto(user));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(convertToDto(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            User userSave = convertToEntity(userDto);
            userSave.setId(id);
            userService.save(userSave);
            return ResponseEntity.ok(convertToDto(userSave));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public UserDto registerUser(@RequestBody UserDto userDto) {
        return convertToDto(authService.register(userDto.getUsername(), userDto.getPassword(), userDto.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        String token = authService.login(userDto.getUsername(), userDto.getPassword());
        TokenDto tokenDto = new TokenDto(token, "Bearer");
        return ResponseEntity.ok(tokenDto);
    }

    private UserDto convertToDto(User user) throws ParseException {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToEntity(UserDto userDto) throws ParseException {
        return modelMapper.map(userDto, User.class);
    }
}


