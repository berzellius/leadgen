package com.leadgen.specifications;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.Order;
import com.leadgen.enumerated.Status;
import com.leadgen.filters.OrderFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by berz on 15.11.14.
 */
public class OrderSpecifications {


    public static Specification<Order> ordersAvailableForClient(final Client client) {
        return new Specification<Order>() {

            @Override
            public Predicate toPredicate(Root<Order> orderRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate inSrcListPredicate = orderRoot.get("orderSource").in(client.getSourceList());
                /*Predicate isOwned = criteriaBuilder.equal(orderRoot.get("client"), client);*/
                Predicate statusAvailable = criteriaBuilder.equal(orderRoot.get("status"), Status.AVAILABLE);

                return criteriaQuery.where(
                        criteriaBuilder.and(
                                inSrcListPredicate,
                                statusAvailable
                        )
                ).getRestriction();
            }
        };
    }

    public static Specification<Order> isOwnedByClient(final Client client) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> orderRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(orderRoot.get("client"), client);
            }
        };
    }

    public static Specification<Order> matchForFilter(final OrderFilter orderFilter) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> orderRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate client = (orderFilter.getClient() != null) ?
                        criteriaBuilder.equal(orderRoot.get("client"), orderFilter.getClient()) : getDummyPredicate(criteriaBuilder);
                Predicate status = (orderFilter.getStatus() != null) ?
                        criteriaBuilder.equal(orderRoot.get("status"), orderFilter.getStatus()) : getDummyPredicate(criteriaBuilder);
                Predicate statusList = (orderFilter.getStatusList() != null) ?
                        orderRoot.get("status").in(orderFilter.getStatusList()) : getDummyPredicate(criteriaBuilder);
                Predicate orderSource = (orderFilter.getOrderSource() != null)?
                        criteriaBuilder.equal(orderRoot.get("orderSource"), orderFilter.getOrderSource()) : getDummyPredicate(criteriaBuilder);
                Predicate sourceUTM = (orderFilter.getSourceUTM() != null)?
                        criteriaBuilder.equal(orderRoot.get("sourceUTM"), orderFilter.getSourceUTM()) : getDummyPredicate(criteriaBuilder);


                return criteriaQuery.where(
                        criteriaBuilder.and(
                                client, status, statusList, orderSource, sourceUTM
                        )
                ).getRestriction();
            }
        };
    }

    public static Specification<Order> byId(final Long id){
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> orderRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(orderRoot.get("id"), id);
            }
        };
    }

    public static Specification<Order> hasClient() {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> orderRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.isNotNull(orderRoot.get("client"));
            }
        };
    }

    public static Predicate getDummyPredicate(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
    }


}

