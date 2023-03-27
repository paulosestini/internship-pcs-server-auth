package com.poli.internship.domain.models;

public class LoginInputModel {
    public static record LoginInput(String code, UserType userType, String redirectUri) {};
}
