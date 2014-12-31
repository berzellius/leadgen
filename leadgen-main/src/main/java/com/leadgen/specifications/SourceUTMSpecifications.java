package com.leadgen.specifications;

import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.OrderSourcePrices;
import com.leadgen.dmodel.SourceUTM;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Created by berz on 27.11.14.
 */
public class SourceUTMSpecifications {

    public static Specification<SourceUTM> forOrderSource(final OrderSource orderSource){
        return new Specification<SourceUTM>() {
            @Override
            public Predicate toPredicate(Root<SourceUTM> sourceUTMRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Subquery<OrderSourcePrices> orderSourcePricesSubquery = criteriaQuery
                        .subquery(OrderSourcePrices.class);

                Root<OrderSourcePrices> orderSourcePricesRoot = orderSourcePricesSubquery.from(OrderSourcePrices.class);
                orderSourcePricesSubquery.select(orderSourcePricesRoot).where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(orderSourcePricesRoot.get("orderSource"), orderSource),
                                criteriaBuilder.equal(orderSourcePricesRoot.get("sourceUTM"), sourceUTMRoot)
                        )
                );

                return criteriaBuilder.not(criteriaBuilder.exists(orderSourcePricesSubquery));
            }
        };
    }

    public static Specification<SourceUTM> notDeleted(){
        return new Specification<SourceUTM>() {
            @Override
            public Predicate toPredicate(Root<SourceUTM> sourceUTMRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(sourceUTMRoot.get("deleted"), true);
            }
        };
    }

    public static Specification<SourceUTM> deleted(){
        return new Specification<SourceUTM>() {
            @Override
            public Predicate toPredicate(Root<SourceUTM> sourceUTMRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(sourceUTMRoot.get("deleted"), false);
            }
        };
    }

}
