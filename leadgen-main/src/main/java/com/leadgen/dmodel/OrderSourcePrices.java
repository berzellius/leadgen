package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by berz on 27.11.14.
 */
@Entity(name = "OrderSourcePrices")
@Table(name = "order_source_prices")
@Access(AccessType.FIELD)
public class OrderSourcePrices extends DModelEntity {

    public OrderSourcePrices(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "order_source_prices_id_generator")
    @SequenceGenerator(name = "order_source_prices_id_generator", sequenceName = "order_source_prices_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private BigDecimal cost;

    private BigDecimal price;

    @JoinColumn(name = "order_source_id")
    @OneToOne
    private OrderSource orderSource;

    @JoinColumn(name = "source_id")
    @OneToOne
    private SourceUTM sourceUTM;

    @Transient
    private BigDecimal doneOrderCost;

    @Transient
    private BigDecimal conversionInDone;

    @Transient
    private BigDecimal conversionHasClientInDone;

    @Transient
    private BigDecimal profitability;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId().equals(
                ((OrderSourcePrices) obj).getId()
        ) && obj instanceof OrderSourcePrices;
    }

    @Override
    public String toString() {
        return null;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderSource getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(OrderSource orderSource) {
        this.orderSource = orderSource;
    }

    public SourceUTM getSourceUTM() {
        return sourceUTM;
    }

    public void setSourceUTM(SourceUTM sourceUTM) {
        this.sourceUTM = sourceUTM;
    }

    public BigDecimal getDoneOrderCost() {
        return doneOrderCost;
    }

    public void setDoneOrderCost(BigDecimal doneOrderCost) {
        this.doneOrderCost = doneOrderCost;
    }

    public BigDecimal getConversionInDone() {
        return conversionInDone;
    }

    public void setConversionInDone(BigDecimal conversionInDone) {
        this.conversionInDone = conversionInDone;
    }

    public BigDecimal getConversionHasClientInDone() {
        return conversionHasClientInDone;
    }

    public void setConversionHasClientInDone(BigDecimal conversionHasClientInDone) {
        this.conversionHasClientInDone = conversionHasClientInDone;
    }

    public BigDecimal getProfitability() {
        return profitability;
    }

    public void setProfitability(BigDecimal profitability) {
        this.profitability = profitability;
    }
}
