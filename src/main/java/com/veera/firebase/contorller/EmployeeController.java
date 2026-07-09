package com.veera.firebase.contorller;

import com.veera.firebase.model.ApiResponse;
import com.veera.firebase.model.Employee;
import com.veera.firebase.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> save(@RequestBody Employee employee) throws Exception {
        String updateTime = service.save(employee);
        return new ResponseEntity<>(new ApiResponse<>("Employee created successfully", updateTime), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> get(@PathVariable String id) throws Exception {
        Employee employee = service.getEmployee(id);
        if (employee != null) {
            return new ResponseEntity<>(new ApiResponse<>("Employee fetched successfully", employee), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("Employee not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> update(@RequestBody Employee employee) throws Exception {
        String updateTime = service.update(employee);
        return new ResponseEntity<>(new ApiResponse<>("Employee updated successfully", updateTime), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) throws Exception {
        String updateTime = service.delete(id);
        return new ResponseEntity<>(new ApiResponse<>("Employee deleted successfully", updateTime), HttpStatus.OK);
    }
}