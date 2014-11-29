package com.leadgen.repository;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.Order;
import com.leadgen.dmodel.OrderHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by berz on 15.11.14.
 */
@Transactional(readOnly = true)
public interface OrderHistoryRepository extends CrudRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderAndClientOrderByDtmCreateDesc(Order order, Client client);
}
