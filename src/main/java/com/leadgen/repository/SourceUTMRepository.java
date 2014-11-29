package com.leadgen.repository;

import com.leadgen.dmodel.SourceUTM;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 27.11.14.
 */
@Transactional(readOnly = true)
public interface SourceUTMRepository extends CrudRepository<SourceUTM, Long>, JpaSpecificationExecutor {
    List<SourceUTM> findByCode(String code);

    List<SourceUTM> findByName(String name);
}
