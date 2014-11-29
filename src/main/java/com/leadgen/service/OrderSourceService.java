package com.leadgen.service;

import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.OrderSourcePrices;
import com.leadgen.dmodel.SourceUTM;
import com.leadgen.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 12.11.14.
 */
@Service
@Transactional
public interface OrderSourceService {
    void addOrderSource(OrderSource orderSource) throws WrongInputDataException;

    void update(OrderSource orderSource) throws WrongInputDataException;

    void addOrderSourcePricesToOrderSource(Long id, Long utmId, BigDecimal price, BigDecimal cost) throws WrongInputDataException;

    void deleteOrderSourcePrices(Long pricesId);

    BigDecimal getCostForOrder(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getPriceForOrder(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getFullCost(OrderSource orderSource);

    BigDecimal getFullCost(OrderSource orderSource, SourceUTM sourceUTM);

    Long getDoneOrdersCount(OrderSource orderSource);

    Long getDoneOrdersCount(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getDoneOrderCost(OrderSource orderSource);

    BigDecimal getDoneOrderCost(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getOrdersConversionInDone(OrderSource orderSource);

    BigDecimal getOrdersConversionInDone(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getOrdersConversionHasClientInDone(OrderSource orderSource);

    BigDecimal getOrdersConversionHasClientInDone(OrderSource orderSource, SourceUTM sourceUTM);

    BigDecimal getProfitability(OrderSource orderSource);

    BigDecimal getProfitability(OrderSource orderSource, SourceUTM sourceUTM);

    void calculateRates(OrderSource orderSource);

    void calculateRatesOrderSourcePrices(OrderSourcePrices orderSourcePrices);

    void calculateRates(List<OrderSource> orderSources);

    void calculateRatesOrderSourcePrices(List<OrderSourcePrices> orderSourcePricesList);
}
