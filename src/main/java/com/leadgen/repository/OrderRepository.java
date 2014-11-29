package com.leadgen.repository;

import com.leadgen.dmodel.Order;
import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.SourceUTM;
import com.leadgen.enumerated.Status;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 06.11.14.
 */
@Transactional(readOnly = true)
public interface OrderRepository extends CrudRepository<Order, Long>, JpaSpecificationExecutor {
    List<Order> findByStatus(Status status);

    @Query(
            "SELECT sum(o.cost) FROM Order o WHERE o.orderSource = :os"
    )
    public BigDecimal fullCostFromOrderSource(@Param(value = "os") OrderSource orderSource);

    @Query(
            "SELECT sum(o.cost) FROM Order o WHERE o.orderSource = :os AND o.sourceUTM = :utm"
    )
    public BigDecimal fullCostFromOrderSourceAndSourceUTM(
            @Param(value = "os") OrderSource orderSource,
            @Param(value = "utm") SourceUTM sourceUTM
    );

    @Query(
            "SELECT  sum(o.price) FROM Order o WHERE o.orderSource = :os AND o.client is not null"
    )
    public BigDecimal fullPriceFromOrderSourceHasClient(
            @Param(value = "os") OrderSource orderSource
    );

    @Query(
            "SELECT  sum(o.price) FROM Order o WHERE " +
                    "o.orderSource = :os AND " +
                    "o.sourceUTM = :utm AND " +
                    "o.client is not null"
    )
    public BigDecimal fullPriceFromOrderSourceAndSourceUTMHasClient(
            @Param(value = "os") OrderSource orderSource,
            @Param(value = "utm") SourceUTM sourceUTM
    );
}
