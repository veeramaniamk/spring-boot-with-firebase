package com.veera.firebase.contorller;

import com.veera.firebase.model.Employee;
import com.veera.firebase.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService service;

    @PostMapping
    public String save(@RequestBody Employee employee) throws Exception {
        return service.save(employee);
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable String id) throws Exception {
        return service.getEmployee(id);
    }

    @PutMapping
    public String update(@RequestBody Employee employee) throws Exception {
        return service.update(employee);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) throws Exception {
        return service.delete(id);
    }
}