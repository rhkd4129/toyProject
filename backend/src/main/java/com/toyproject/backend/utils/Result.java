package com.toyproject.backend.utils;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Result<T> {

    private String message;
    private T data;

}
