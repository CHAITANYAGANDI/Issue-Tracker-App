package com.example.demo;

public class IssueNotFoundException extends RuntimeException{

    public IssueNotFoundException(String message){

        super(message);
    }
}
