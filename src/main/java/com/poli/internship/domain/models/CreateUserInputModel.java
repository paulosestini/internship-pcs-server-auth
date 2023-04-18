package com.poli.internship.domain.models;

public class CreateUserInputModel {
    public static record CreateUserInput(String name, String email, UserType userType, String profilePictureUrl) {}
}
