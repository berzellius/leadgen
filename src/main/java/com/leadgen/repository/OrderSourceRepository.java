package com.leadgen.repository;

import com.leadgen.dmodel.OrderSource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 12.11.14.
 */
@Transactional(readOnly = true)
public interface OrderSourceRepository extends CrudRepository<OrderSource, Long> {
    List<OrderSource> findByAuthKey(String authKey);

    @Query(
        "SELECT os FROM OrderSource os WHERE os NOT IN :srcList "
    )
    List<OrderSource> findSourcesNotExistsInSourcesList(@Param(value = "srcList") List<OrderSource> orderSources);
}
