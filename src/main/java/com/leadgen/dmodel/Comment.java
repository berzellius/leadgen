package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
@Entity(name = "Comment")
@Table(name = "comments")
@Access(AccessType.FIELD)
public class Comment extends DModelEntityFiscalable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_id_generator")
    @SequenceGenerator(name = "comment_id_generator", sequenceName = "comment_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(columnDefinition = "character varying(2000)")
    private String text;

    @JoinColumn(name = "ticket_id")
    @OneToOne
    private Ticket ticket;

    @JoinColumn(name = "order_id")
    @OneToOne
    private Order order;

    @JoinColumn(name = "user_id")
    @OneToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<UploadedFile> attachments;

    private Boolean disabled;

    public Comment(){}

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
                ((Comment) obj).getId()
        ) && obj instanceof Comment;
    }

    @Override
    public String toString() {
        return this.getText();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<UploadedFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<UploadedFile> attachments) {
        this.attachments = attachments;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
