package com.poli.internship.domain.models;

public class UserModel {
    public static record User(String id, String name, String email, UserType userType, String profilePictureUrl) {};

}
