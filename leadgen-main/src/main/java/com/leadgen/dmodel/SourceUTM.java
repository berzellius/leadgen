package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 27.11.14.
 */
@Entity(name = "Source")
@Table(name = "sources_utm")
@Access(AccessType.FIELD)
public class SourceUTM extends DModelEntity {

    public SourceUTM(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "source_utm_id_generator")
    @SequenceGenerator(name = "source_utm_id_generator", sequenceName = "source_utm_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "Boolean default false")
    private Boolean deleted;

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
                ((SourceUTM) obj).getId()
        ) && obj instanceof SourceUTM;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
