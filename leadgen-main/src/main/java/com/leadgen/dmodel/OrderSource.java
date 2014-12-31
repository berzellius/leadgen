package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by berz on 07.11.14.
 */
@Entity(name = "OrderSource")
@Table(
        name = "order_sources",
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"auth_key"})
)
public class OrderSource extends DModelEntityNamed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "order_source_id_generator")
    @SequenceGenerator(name = "order_source_id_generator", sequenceName = "order_source_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private String url;

    @Column(name = "auth_key", unique = true, updatable = false)
    private String authKey;

    private BigDecimal cost;

    private BigDecimal price;

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
    public String toString(){
        return this.getName() + ": " + this.getDescription();
    }

    @Override
    public boolean equals(Object obj){
        return this.getId().equals(
                ((OrderSource) obj).getId()
        ) && obj instanceof OrderSource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
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
