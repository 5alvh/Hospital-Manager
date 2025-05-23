package com.tfg.back.mappers;

import com.tfg.back.model.Department;
import com.tfg.back.model.Doctor;
import com.tfg.back.model.dtos.department.DepartmentDtoCreate;
import com.tfg.back.model.dtos.department.DepartmentDtoUpdate;

public class DepartmentMapper {


    public static Department toEntity(DepartmentDtoCreate dto) {
        if (dto == null) {
            return null;
        }
        Department department = new Department();

        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setContactNumber(dto.getContactNumber());
        department.setLocation(dto.getLocation());

        return department;
    }

    public static Department updateEntity(Department department, DepartmentDtoUpdate dto, Doctor headDoctor) {
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setContactNumber(dto.getContactNumber());
        department.setLocation(dto.getLocation());

        // Only update head doctor if a new one is provided
        if (headDoctor != null) {
            department.setHeadDoctor(headDoctor);
        }
        return department;
    }
}
