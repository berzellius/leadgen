package com.leadgen.dmodel;


import com.leadgen.attrconverters.OrderFromSourceConverter;
import com.leadgen.enumerated.Source;
import com.leadgen.enumerated.Status;
import com.leadgen.json.OrderFromSource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by berz on 20.10.14.
 */
@Entity(name = "Order")
@Table(name = "orders")
@Access(AccessType.FIELD)
public class
        Order extends DModelEntityFiscalable{

    public Order(){

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OrderSource getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(OrderSource orderSource) {
        this.orderSource = orderSource;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public OrderFromSource getInfoJson() {
        return infoJson;
    }

    public void setInfoJson(OrderFromSource infoJson) {
        this.infoJson = infoJson;
    }

    /*public enum Status{
        AVAILABLE,
        PROCESSING,
        DONE;
    }*/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "order_id_generator")
    @SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Override
    public Long getId(){
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "info")
    private String info;

    @Column(
            name = "infojson"
    )
    @Convert(converter = OrderFromSourceConverter.class)
    private OrderFromSource infoJson;

    @Enumerated(value = EnumType.STRING)
    private Source source;

    @JoinColumn(name = "source_utm_id")
    @OneToOne
    private SourceUTM sourceUTM;

    @JoinColumn(name = "order_src_id")
    @OneToOne
    private OrderSource orderSource;

    @Enumerated(value = EnumType.STRING)
    private Status status;


    @JoinColumn(name = "client_id")
    @OneToOne
    private Client client;

    private BigDecimal cost;

    private BigDecimal price;


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }



    @Override
    public String toString(){
        return this.getInfo() + ": " + this.getSource().toString();
    }

    @Override
    public boolean equals(Object obj){
        return this.getId().equals(
                ((Order) obj).getId()
        ) && obj instanceof Order;
    }


    public SourceUTM getSourceUTM() {
        return sourceUTM;
    }

    public void setSourceUTM(SourceUTM sourceUTM) {
        this.sourceUTM = sourceUTM;
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
}
