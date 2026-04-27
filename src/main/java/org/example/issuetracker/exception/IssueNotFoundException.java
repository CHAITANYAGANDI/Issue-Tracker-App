package org.example.issuetracker.exception;

public class IssueNotFoundException extends RuntimeException{

    public IssueNotFoundException(String message){

        super(message);
    }
}
