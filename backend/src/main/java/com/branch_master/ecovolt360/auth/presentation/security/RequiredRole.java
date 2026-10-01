package com.branch_master.ecovolt360.auth.presentation.security;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole {
    Role value();
}
