package com.example.RESTdemo;

import org.springframework.data.jpa.repository.JpaRepository;

// Just by making this empty interface JPA can create / update / delete / find data entries
// We just tell them we are looking for Employees with ID type long
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
