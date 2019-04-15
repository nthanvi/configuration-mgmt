package com.sample.poc.security;


//Todo replace with config later
public class SecurityConstants {
        public static final String SECRET = "SecretKeyToGenJWTs";
        public static final long EXPIRATION_TIME = 600_000; // 10 minutes
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";
        public static final String SIGN_UP_URL = "/sign-up";
        public static final String SWAGGER_URL = "/swagger-ui.html";
}
