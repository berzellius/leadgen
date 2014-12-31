package com.leadgen.repository;

import com.leadgen.dmodel.Comment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 19.11.14.
 */
@Transactional(readOnly = true)
public interface CommentRepository extends CrudRepository<Comment, Long>, JpaSpecificationExecutor {

}
