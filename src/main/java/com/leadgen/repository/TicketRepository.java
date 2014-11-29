package com.leadgen.repository;

import com.leadgen.dmodel.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 19.11.14.
 */
@Transactional(readOnly = true)
public interface TicketRepository extends CrudRepository<Ticket, Long>, JpaSpecificationExecutor {
}
