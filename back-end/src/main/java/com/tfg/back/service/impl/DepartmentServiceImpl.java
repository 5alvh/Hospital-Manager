package com.tfg.back.service.impl;

import com.tfg.back.enums.SearchType;
import com.tfg.back.exceptions.department.DepartmentAlreadyExistsException;
import com.tfg.back.exceptions.department.DepartmentNotFoundException;
import com.tfg.back.exceptions.user.UserNotFoundException;
import com.tfg.back.mappers.DepartmentMapper;
import com.tfg.back.model.Department;
import com.tfg.back.model.Doctor;
import com.tfg.back.model.dtos.department.DepartmentDtoCreate;
import com.tfg.back.model.dtos.department.DepartmentDtoUpdate;
import com.tfg.back.repository.DepartmentRepository;
import com.tfg.back.repository.DoctorRepository;
import com.tfg.back.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.tfg.back.constants.ResponseMessages.*;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DoctorRepository doctorRepository) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Department createDepartment(DepartmentDtoCreate createDto) {
        boolean exists = departmentRepository.existsByName(createDto.getName());
        if(exists){
            throw new DepartmentAlreadyExistsException(createDto.getName());
        }
        Department dep = DepartmentMapper.toEntity(createDto);
        return departmentRepository.save(dep);
    }

    @Override
    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(()-> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_WITH_NAME));
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(()-> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_WITH_ID));
    }

    @Override
    public Department updateDepartment(Long id, DepartmentDtoUpdate departmentUpdateDto) {
        String name = departmentUpdateDto.getName();
        /*
        //Exists by Name?
        boolean existsByName = departmentRepository.existsByName(name);
        //Exists is it the same department or another who has the same name?
        boolean sameID = getDepartmentByName(name).getId().equals(id);
        //If it's the same department then it's ok. if not, throw error that it already exists
        if(existsByName && !sameID){
            throw new DepartmentAlreadyExistsException(departmentUpdateDto.getName());
        }
        */
        Optional<Department> existing = departmentRepository.findByName(name);
        existing.ifPresent(ex -> {
            if (!ex.getId().equals(id)) {
                throw new DepartmentAlreadyExistsException(name);
        }});

        String email = departmentUpdateDto.getHeadDoctorEmail();
        Doctor doctor= null;

        if (email != null) {
            doctor = doctorRepository.findByEmail(email)
                    .orElseThrow(()-> new UserNotFoundException(email, SearchType.EMAIL));
        }

        Department department = DepartmentMapper.updateEntity(getDepartmentById(id), departmentUpdateDto,doctor);
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.deleteById(id);
    }


}
