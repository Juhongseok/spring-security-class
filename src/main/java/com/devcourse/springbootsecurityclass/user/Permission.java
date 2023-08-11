package com.devcourse.springbootsecurityclass.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PERMISSIONS")
public class Permission {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    protected Permission() {
    }

    public Permission(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
