package com.devcourse.springbootsecurityclass.user;

import com.devcourse.springbootsecurityclass.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final Jwt jwt;

    private final UserService userService;

    public UserRestController(Jwt jwt, UserService userService) {
        this.jwt = jwt;
        this.userService = userService;
    }

    @GetMapping(path = "/user/me")
    public String me() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping(path = "/user/{username}/token")
    public String getToken(@PathVariable String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        String[] roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
        return jwt.sign(Jwt.Claims.from(userDetails.getUsername(), roles));
    }

    @GetMapping(path = "/user/token/verify")
    public Map<String, Object> verify(@RequestHeader("token") String token) {
        return jwt.verify(token).asMap();
    }

}
