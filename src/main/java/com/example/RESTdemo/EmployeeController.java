package com.example.RESTdemo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
// Annotation - data returned from each method will be written straight into the response body (instead of rendering a template)
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    // Constructor injection
    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Following methods correspond to GET, POST, PUT, DELETE

    // RPC VERSION
//    @GetMapping("/employees")
//    List<Employee> all() {
//        return repository.findAll();
//    }

    // REST VERSION (for an aggreate root source)
    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {      // CollectionModel<> is a Spring HATEOAS container that encapsulates collections of (employee) resources
//        List<EntityModel<Employee>> employees = repository.findAll().stream().map(employee -> EntityModel.of(
//                employee,
//                linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
//                linkTo(methodOn(EmployeeController.class).all()).withRel("employees"))).collect(Collectors.toList());

        List<EntityModel<Employee>> employees = repository
                .findAll().stream().map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());

    }


    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));
        return ResponseEntity.created(entityModel           // ResponseEntity creates an HTTP 201 Created status message
                .getRequiredLink(IanaLinkRelations.SELF)    // With a location response header
                .toUri()).body(entityModel);
    }

    // RPC VERSION
//    @GetMapping("/employees/{id}")
//    Employee one(@PathVariable Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new EmployeeNotFoundException(id));
//    }

    // REST VERSION
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {      // EntityModel<T> is a generic Spring HATEOAS that includes data and a collection of links
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return assembler.toModel(employee);
//        return EntityModel.of(employee,
//                linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),       // Ask Spring HATEOAS to build a link to the EmployeeController's one() method and flag it as a self link
//                linkTo(methodOn(EmployeeController.class).all()).withRel("employees")); // Ask Spring HATEOAS to build a link the the aggregate root all() and call it "employees"
    }
    // See Google notes on building links

    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id){

        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });

        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
