package com.veera.firebase.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Employee {

    private String id;
    private String name;
    private int age;
    private String department;

    public Employee() {
    }

    public Employee(String id, String name, int age, String department) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
    }

    // getters setters
}