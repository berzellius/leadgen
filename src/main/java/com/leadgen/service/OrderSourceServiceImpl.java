package com.leadgen.service;

import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.OrderSourcePrices;
import com.leadgen.dmodel.SourceUTM;
import com.leadgen.enumerated.Status;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.filters.OrderFilter;
import com.leadgen.repository.OrderRepository;
import com.leadgen.repository.OrderSourcePricesRepository;
import com.leadgen.repository.OrderSourceRepository;
import com.leadgen.repository.SourceUTMRepository;
import com.leadgen.specifications.OrderSpecifications;
import com.leadgen.specifications.SourceUTMSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;


/**
 * Created by berz on 12.11.14.
 */
@Service
@Transactional
public class OrderSourceServiceImpl implements OrderSourceService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    OrderSourceRepository orderSourceRepository;

    @Autowired
    SourceUTMRepository sourceUTMRepository;

    @Autowired
    OrderSourcePricesRepository orderSourcePricesRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void addOrderSource(OrderSource orderSource) throws WrongInputDataException {
        validateOrderSource(orderSource);

         if(!saveNewOrderSource(orderSource)){
            // fuckup
         }
    }

    @Override
    public void update(OrderSource orderSource) throws WrongInputDataException {
        validateOrderSource(orderSource);

        orderSourceRepository.save(orderSource);
    }

    @Override
    public void addOrderSourcePricesToOrderSource(Long id, Long utmId, BigDecimal price, BigDecimal cost) throws WrongInputDataException {
        OrderSource orderSource = orderSourceRepository.findOne(id);
        if(orderSource == null) return;

        SourceUTM sourceUTM = sourceUTMRepository.findOne(utmId);
        if(sourceUTM == null) return;

        // Проверка, есть ли параметры с таким utm
        List<SourceUTM> sourceUTMListForOrderSource = sourceUTMRepository.findAll(
                Specifications.where(
                        SourceUTMSpecifications.notDeleted()
                )
                .and(
                        SourceUTMSpecifications.forOrderSource(orderSource)
                )
        );

        if(! sourceUTMListForOrderSource.contains(sourceUTM)) throw new WrongInputDataException("one utm prices for one orderSource!", WrongInputDataException.Reason.UNIQUE);

        if(cost.compareTo(BigDecimal.ZERO) < 1) throw new WrongInputDataException("cost must be more than 0", WrongInputDataException.Reason.COST_FIELD);

        if(price.compareTo(BigDecimal.ZERO) < 1) throw new WrongInputDataException("price must be more than 0", WrongInputDataException.Reason.PRICE_FIELD);

        OrderSourcePrices orderSourcePrices = new OrderSourcePrices();
        orderSourcePrices.setOrderSource(orderSource);
        orderSourcePrices.setSourceUTM(sourceUTM);
        orderSourcePrices.setCost(cost);
        orderSourcePrices.setPrice(price);

        orderSourcePricesRepository.save(orderSourcePrices);
    }

    @Override
    public void deleteOrderSourcePrices(Long pricesId) {
        OrderSourcePrices orderSourcePrices = orderSourcePricesRepository.findOne(pricesId);

        if(orderSourcePrices != null){
            orderSourcePricesRepository.delete(orderSourcePrices);
        }
    }

    @Override
    public BigDecimal getCostForOrder(OrderSource orderSource, SourceUTM sourceUTM) {
        List<OrderSourcePrices> orderSourcePricesList = orderSourcePricesRepository.findByOrderSourceAndSourceUTM(orderSource, sourceUTM);

        if(!orderSourcePricesList.isEmpty()){
            return orderSourcePricesList.get(0).getCost();
        }
        else return orderSource.getCost();

    }

    @Override
    public BigDecimal getPriceForOrder(OrderSource orderSource, SourceUTM sourceUTM) {
        List<OrderSourcePrices> orderSourcePricesList = orderSourcePricesRepository.findByOrderSourceAndSourceUTM(orderSource, sourceUTM);

        if(!orderSourcePricesList.isEmpty()){
            return orderSourcePricesList.get(0).getPrice();
        }
        else return orderSource.getPrice();
    }

    @Override
    public BigDecimal getFullCost(OrderSource orderSource) {
        return orderRepository.fullCostFromOrderSource(orderSource);
    }

    @Override
    public BigDecimal getFullCost(OrderSource orderSource, SourceUTM sourceUTM) {
        return orderRepository.fullCostFromOrderSourceAndSourceUTM(orderSource, sourceUTM);
    }


    @Override
    public Long getDoneOrdersCount(OrderSource orderSource) {
        OrderFilter orderFilter = new OrderFilter();
        orderFilter.setOrderSource(orderSource);
        orderFilter.setStatus(Status.DONE);

        return orderRepository.count(OrderSpecifications.matchForFilter(orderFilter));
    }

    @Override
    public Long getDoneOrdersCount(OrderSource orderSource, SourceUTM sourceUTM) {
        OrderFilter orderFilter = new OrderFilter();
        orderFilter.setOrderSource(orderSource);
        orderFilter.setSourceUTM(sourceUTM);
        orderFilter.setStatus(Status.DONE);

        return orderRepository.count(OrderSpecifications.matchForFilter(orderFilter));
    }

    @Override
    public BigDecimal getDoneOrderCost(OrderSource orderSource) {
        Long doneOrders = getDoneOrdersCount(orderSource);

        if(doneOrders == null || doneOrders.equals(0l)) return null;

        BigDecimal fullCost = getFullCost(orderSource);

        if(fullCost == null || fullCost.compareTo(BigDecimal.ZERO) == 0) return null;

        return getFullCost(orderSource).divide(BigDecimal.valueOf(doneOrders), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getDoneOrderCost(OrderSource orderSource, SourceUTM sourceUTM) {
        Long doneOrders = getDoneOrdersCount(orderSource, sourceUTM);

        if(doneOrders == null || doneOrders.equals(0l)) return null;

        BigDecimal fullCost = getFullCost(orderSource, sourceUTM);

        if(fullCost == null || fullCost.compareTo(BigDecimal.ZERO) == 0) return null;

        return getFullCost(orderSource).divide(BigDecimal.valueOf(doneOrders), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getOrdersConversionInDone(OrderSource orderSource) {
        OrderFilter orderFilterAll = new OrderFilter();
        orderFilterAll.setOrderSource(orderSource);

        Long all = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterAll));

        OrderFilter orderFilterDone = new OrderFilter();
        orderFilterDone.setOrderSource(orderSource);
        orderFilterDone.setStatus(Status.DONE);

        Long done = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterDone));

        if(all == null || all.equals(0l)) return null;

        return BigDecimal.valueOf(done).divide(BigDecimal.valueOf(all), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getOrdersConversionInDone(OrderSource orderSource, SourceUTM sourceUTM) {
        OrderFilter orderFilterAll = new OrderFilter();
        orderFilterAll.setOrderSource(orderSource);
        orderFilterAll.setSourceUTM(sourceUTM);

        Long all = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterAll));

        OrderFilter orderFilterDone = new OrderFilter();
        orderFilterDone.setOrderSource(orderSource);
        orderFilterDone.setSourceUTM(sourceUTM);
        orderFilterDone.setStatus(Status.DONE);

        Long done = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterDone));

        if(all == null || all.equals(0l)) return null;

        return BigDecimal.valueOf(done).divide(BigDecimal.valueOf(all), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getOrdersConversionHasClientInDone(OrderSource orderSource) {
        OrderFilter orderFilterAll = new OrderFilter();
        orderFilterAll.setOrderSource(orderSource);

        Long all = orderRepository.count(
                Specifications
                        .where(
                                OrderSpecifications.matchForFilter(orderFilterAll)
                        )
                        .and(
                                OrderSpecifications.hasClient()
                        )

        );

        OrderFilter orderFilterDone = new OrderFilter();
        orderFilterDone.setOrderSource(orderSource);
        orderFilterDone.setStatus(Status.DONE);

        Long done = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterDone));

        if(all == null || all.equals(0l)) return null;

        return BigDecimal.valueOf(done).divide(BigDecimal.valueOf(all), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getOrdersConversionHasClientInDone(OrderSource orderSource, SourceUTM sourceUTM) {
        OrderFilter orderFilterAll = new OrderFilter();
        orderFilterAll.setOrderSource(orderSource);
        orderFilterAll.setSourceUTM(sourceUTM);

        Long all = orderRepository.count(
                Specifications
                        .where(
                                OrderSpecifications.matchForFilter(orderFilterAll)
                        )
                        .and(
                                OrderSpecifications.hasClient()
                        )

        );

        OrderFilter orderFilterDone = new OrderFilter();
        orderFilterDone.setOrderSource(orderSource);
        orderFilterDone.setSourceUTM(sourceUTM);
        orderFilterDone.setStatus(Status.DONE);

        Long done = orderRepository.count(OrderSpecifications.matchForFilter(orderFilterDone));

        if(all == null || all.equals(0l)) return null;

        return BigDecimal.valueOf(done).divide(BigDecimal.valueOf(all), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getProfitability(OrderSource orderSource) {
        BigDecimal allCost = orderRepository.fullCostFromOrderSource(orderSource);
        BigDecimal allProfit = orderRepository.fullPriceFromOrderSourceHasClient(orderSource);

        if(allCost == null || allCost.compareTo(BigDecimal.ZERO) == 0) return  null;

        return allProfit.divide(allCost, 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getProfitability(OrderSource orderSource, SourceUTM sourceUTM) {
        BigDecimal allCost = orderRepository.fullCostFromOrderSourceAndSourceUTM(orderSource, sourceUTM);
        BigDecimal allProfit = orderRepository.fullPriceFromOrderSourceAndSourceUTMHasClient(orderSource, sourceUTM);

        if(allCost == null || allCost.compareTo(BigDecimal.ZERO) == 0) return  null;
        if(allProfit == null || allProfit.compareTo(BigDecimal.ZERO) == 0) return null;

        return allProfit.divide(allCost, 2, RoundingMode.HALF_UP);
    }

    @Override
    public void calculateRates(OrderSource orderSource) {
        orderSource.setConversionHasClientInDone(getOrdersConversionHasClientInDone(orderSource));
        orderSource.setConversionInDone(getOrdersConversionInDone(orderSource));
        orderSource.setDoneOrderCost(getDoneOrderCost(orderSource));
        orderSource.setProfitability(getProfitability(orderSource));
    }

    @Override
    public void calculateRatesOrderSourcePrices(OrderSourcePrices orderSourcePrices) {
        orderSourcePrices.setConversionHasClientInDone(
                getOrdersConversionHasClientInDone(
                        orderSourcePrices.getOrderSource(),
                        orderSourcePrices.getSourceUTM())
        );

        orderSourcePrices.setConversionInDone(
                getOrdersConversionInDone(
                        orderSourcePrices.getOrderSource(),
                        orderSourcePrices.getSourceUTM())
        );

        orderSourcePrices.setDoneOrderCost(
                getDoneOrderCost(
                        orderSourcePrices.getOrderSource(),
                        orderSourcePrices.getSourceUTM())
        );

        orderSourcePrices.setProfitability(
                getProfitability(
                        orderSourcePrices.getOrderSource(),
                        orderSourcePrices.getSourceUTM())
        );
    }

    @Override
    public void calculateRates(List<OrderSource> orderSources) {
        for(OrderSource orderSource : orderSources){
            calculateRates(orderSource);
        }
    }

    @Override
    public void calculateRatesOrderSourcePrices(List<OrderSourcePrices> orderSourcePricesList) {
        for(OrderSourcePrices orderSourcePrices : orderSourcePricesList){
            calculateRatesOrderSourcePrices(orderSourcePrices);
        }
    }


    private void validateOrderSource(OrderSource orderSource) throws WrongInputDataException {
        if(orderSource.getName() == null || orderSource.getName().equals(""))
            throw new WrongInputDataException("empty name parameter given for new/exist order source!", WrongInputDataException.Reason.NAME_FIELD);

        if(orderSource.getUrl() == null || orderSource.getUrl().equals(""))
            throw new WrongInputDataException("emprty url parameter given for new/exists order source", WrongInputDataException.Reason.URL_FIELD);

        if(orderSource.getCost() == null || orderSource.getCost().compareTo(BigDecimal.ZERO) < 1)
            throw new WrongInputDataException("cost parameter given for new/exists order source mst more than 0", WrongInputDataException.Reason.COST_FIELD);

        if(orderSource.getPrice() == null || orderSource.getPrice().compareTo(BigDecimal.ZERO) < 1)
            throw new WrongInputDataException("price parameter given for new/exists order source mst more than 0", WrongInputDataException.Reason.PRICE_FIELD);

    }

    private boolean saveNewOrderSource(OrderSource orderSource){
        orderSource.setAuthKey(this.generateAuthKey(orderSource));

        orderSourceRepository.save(orderSource);

        return true;
    }

    private String generateAuthKey(OrderSource orderSource) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


}
