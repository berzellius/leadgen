package com.leadgen.specifications;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.Ticket;
import com.leadgen.dmodel.User;
import com.leadgen.enumerated.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by berz on 19.11.14.
 */
public class TicketSpecifications {

    public static Specification<Ticket> ticketsForAdmin(){
        return new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> ticketRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return getDummyPredicate(criteriaBuilder);
            }
        };
    }

    public static Specification<Ticket> ticketsForClient(final Client client){
        return new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> ticketRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(ticketRoot.get("client"), client);

            }
        };
    }

    public static Specification<Ticket> ticketsForManager(final User user){
        return new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> ticketRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(ticketRoot.get("owner"), user);
            }
        };
    }

    public static Specification<Ticket> notClosed(){
        return new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> ticketRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(ticketRoot.get("status"), TicketStatus.CLOSED);
            }
        };
    }

    public static Specification<Ticket> isClosed(){
        return new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> ticketRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(ticketRoot.get("status"), TicketStatus.CLOSED);
            }
        };
    }


    public static Predicate getDummyPredicate(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
    }
}
