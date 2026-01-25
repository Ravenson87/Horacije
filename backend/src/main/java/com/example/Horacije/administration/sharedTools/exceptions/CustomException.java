package com.example.Horacije.administration.sharedTools.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

    // Ovo ubacujuem na preporuku kopilota
    // Kada imam gresku da moze da mi baci poruku, ali i orginalni razlog sta je greska
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

}
