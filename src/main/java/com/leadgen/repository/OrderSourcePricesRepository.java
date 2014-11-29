package com.leadgen.repository;

import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.OrderSourcePrices;
import com.leadgen.dmodel.SourceUTM;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 27.11.14.
 */
@Transactional(readOnly = true)
public interface OrderSourcePricesRepository extends CrudRepository<OrderSourcePrices, Long> {
    List<OrderSourcePrices> findByOrderSourceAndSourceUTM(OrderSource orderSource, SourceUTM sourceUTM);

    List<OrderSourcePrices> findByOrderSource(OrderSource orderSource);
}
