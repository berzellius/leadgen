package com.leadgen.specifications;

import com.leadgen.dmodel.Comment;
import com.leadgen.dmodel.Order;
import com.leadgen.dmodel.Ticket;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by berz on 19.11.14.
 */
public class CommentSpecifications {

    public static Specification<Comment> forOrder(final Order order){
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> commentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(commentRoot.get("order"), order);
            }
        };
    }

    public static Specification<Comment> forTicket(final Ticket ticket){
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> commentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("dtmCreate")));

                return criteriaBuilder.equal(commentRoot.get("ticket"), ticket);
            }
        };
    }

    public static Specification<Comment> orderByDtmCreateAsc(){
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> commentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                return criteriaQuery.orderBy(criteriaBuilder.asc(commentRoot.get("dtmCreate"))).getRestriction();
            }
        };
    }

    public static Specification<Comment> notDisabled(){
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> commentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(commentRoot.get("disabled"), true);
            }
        };
    }

    public static Specification<Comment> disabled(){
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> commentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(commentRoot.get("disabled"), true);
            }
        };
    }

}
