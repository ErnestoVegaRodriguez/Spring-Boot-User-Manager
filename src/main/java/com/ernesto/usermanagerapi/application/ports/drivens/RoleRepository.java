package com.ernesto.usermanagerapi.application.ports.drivens;

import java.util.List;

import com.ernesto.usermanagerapi.domain.entities.Role;
import com.ernesto.usermanagerapi.domain.exceptions.NotFoundException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

public interface RoleRepository {

    public List<Role> findAll();

    public Result<Role, NotFoundException> findById(int id);

    public Result<Role, NotFoundException> findByName(String name);

    public Role add(Role entity);

    public Role update(Role entity);

    public void delete(int id);

}
