package com.devcourse.springbootsecurityclass.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name = "GROUPS")
public class Group {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "groups", cascade = ALL, orphanRemoval = true, fetch = EAGER)
    private List<GroupPermission> groupPermissions = new ArrayList<>();

    protected Group() {
    }

    public Group(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<GroupPermission> getGroupPermissions() {
        return groupPermissions;
    }

    public List<? extends GrantedAuthority> getPermissions() {
        return groupPermissions.stream()
                .map(GroupPermission::getPermission)
                .map(Permission::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
