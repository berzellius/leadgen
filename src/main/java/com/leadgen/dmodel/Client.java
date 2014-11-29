package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 07.11.14.
 */
@Entity(name = "Client")
@Table(name = "clients")
@Access(value = AccessType.FIELD)
public class Client extends DModelEntityNamed {

    public Client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "client_id_generator")
    @SequenceGenerator(name = "client_id_generator", sequenceName = "client_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private BigDecimal money;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_sources_list",
            joinColumns = {
                    @JoinColumn(name = "client_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "src_id")
            }
    )
    private List<OrderSource> sourceList;

    @Override
    public String toString(){
        return this.getName() + ": " + this.getDescription();
    }

    @Override
    public boolean equals(Object obj){
        return this.getId().equals(
                ((Client) obj).getId()
        ) && obj instanceof Client;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public List<OrderSource> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<OrderSource> sourceList) {
        this.sourceList = sourceList;
    }
}
