package ru.tersoft.ticketsale.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dialogs")
@ApiModel(value = "Dialog")
public class Dialog implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name = "title", nullable = false)
    @ApiModelProperty(required = true)
    private String title;

    @OneToMany(mappedBy = "dialog", cascade = CascadeType.ALL)
    @ApiModelProperty(required = true)
    @OrderBy("date ASC")
    private List<Message> messages;

    @Column(name = "closed")
    private Boolean closed;

    @Column(name = "answered")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean answered;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
