package com.leadgen.repository;

import com.leadgen.dmodel.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 08.11.14.
 */
@Transactional(readOnly = true)
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

    @Query("SELECT r FROM UserRole r WHERE role = :r")
    List<UserRole> findByRole(@Param("r") UserRole.Role role);

}
