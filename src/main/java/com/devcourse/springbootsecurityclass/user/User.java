package com.devcourse.springbootsecurityclass.user;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "passwd")
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    protected User() {
    }

    public User(Long id, String loginId, String password, Group group) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.group = group;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public Group getGroup() {
        return group;
    }
}
