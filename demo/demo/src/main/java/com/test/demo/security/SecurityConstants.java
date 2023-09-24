package com.test.demo.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 700000000;
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
}
