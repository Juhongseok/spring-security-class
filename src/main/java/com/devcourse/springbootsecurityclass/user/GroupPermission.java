package com.devcourse.springbootsecurityclass.user;

import jakarta.persistence.*;

@Entity
@Table(name = "GROUP_PERMISSION")
public class GroupPermission {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group groups;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    protected GroupPermission() {
    }

    public GroupPermission(Long id, Group groups, Permission permission) {
        this.id = id;
        this.groups = groups;
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
}
