package com.caiohenrique.demo_park_api.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;


public class JwtUserDetails extends User {

    private com.caiohenrique.demo_park_api.entity.User user;

    public JwtUserDetails(com.caiohenrique.demo_park_api.entity.User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getRole() {
        return this.user.getRole().name();
    }
}
