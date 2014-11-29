package com.leadgen.dmodel;

import com.leadgen.enumerated.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 06.11.14.
 */
@Entity(name = "OrderHistory")
@Table(name = "order_history")
@Access(value = AccessType.FIELD)
public class OrderHistory extends DModelEntityFiscalable {

    public OrderHistory(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "order_history_id_generator")
    @SequenceGenerator(name = "order_history_id_generator", sequenceName = "order_history_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @JoinColumn(name = "order_id")
    @OneToOne
    private Order order;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @JoinColumn(name = "client_id")
    @OneToOne
    private Client client;

    @Override
    public String toString(){
        return this.getOrder().toString() + ": " + this.getStatus().toString() + ", " + this.getDtmCreate();
    }

    @Override
    public boolean equals(Object obj){
        return this.getId().equals(
                ((OrderHistory) obj).getId()
        ) && obj instanceof OrderHistory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
