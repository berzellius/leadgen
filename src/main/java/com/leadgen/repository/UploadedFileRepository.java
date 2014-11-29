package com.leadgen.repository;

import com.leadgen.dmodel.UploadedFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 23.11.14.
 */
@Transactional(readOnly = true)
public interface UploadedFileRepository extends CrudRepository<UploadedFile, Long>,JpaSpecificationExecutor {

}
