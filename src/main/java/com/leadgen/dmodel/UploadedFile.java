package com.leadgen.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 22.11.14.
 */
@Entity(name = "UploadedFile")
@Table(name = "uploaded_files")
@Access(AccessType.FIELD)
public class UploadedFile extends DModelEntityNamed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uploaded_files_id_generator")
    @SequenceGenerator(name = "uploaded_files_id_generator", sequenceName = "uploaded_files_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private String filename;

    private String path;

    private String extension;

    @Column(name = "mime_type")
    private String mimeType;

    @JoinColumn(name = "ticket_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Ticket ticket;

    @JoinColumn(name = "comment_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Comment comment;

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
                ((UploadedFile) obj).getId()
        ) && obj instanceof UploadedFile;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
