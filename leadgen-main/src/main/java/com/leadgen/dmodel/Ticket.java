package com.leadgen.dmodel;

import com.leadgen.enumerated.TicketStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
@Entity(name = "Ticket")
@Table(name = "tickets")
@Access(AccessType.FIELD)
public class Ticket extends DModelEntityNamed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ticket_id_generator")
    @SequenceGenerator(name = "ticket_id_generator", sequenceName = "ticket_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private TicketStatus status;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User owner;

    @JoinColumn(name = "client_id")
    @OneToOne
    private Client client;


    @OneToMany(mappedBy = "ticket")
    private List<UploadedFile> attachments;


    public Ticket(){}

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj){
        return this.getId().equals(((Ticket) obj).getId()) &&
                obj instanceof Ticket;
    }

    @Override
    public String toString(){
        return this.getName();
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<UploadedFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<UploadedFile> attachments) {
        this.attachments = attachments;
    }
}
