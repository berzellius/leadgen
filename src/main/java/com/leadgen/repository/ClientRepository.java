package com.leadgen.repository;

import com.leadgen.dmodel.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 07.11.14.
 */
@Transactional(readOnly = true)
public interface ClientRepository extends CrudRepository<Client, Long> {

}
