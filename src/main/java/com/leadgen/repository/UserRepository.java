package com.leadgen.repository;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.UserRole;
import com.leadgen.dmodel.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 08.11.14.
 */
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(
            "SELECT u FROM User u WHERE " +
            "client = :client AND " +
            "EXISTS (SELECT ur FROM u.authorities ur WHERE ur.role = :role)"
           )
    List<User> findByClientHasRole(@Param("client") Client client, @Param("role") UserRole.Role userRole);

    User findByUsername(String username);
}
