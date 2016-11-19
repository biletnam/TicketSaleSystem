package ru.tersoft.entity;

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

    @OneToMany(mappedBy="dialog", cascade= CascadeType.ALL)
    @ApiModelProperty(value = "questions", required = true)
    private List<Question> questions;

    @OneToMany(mappedBy="dialog", cascade= CascadeType.ALL)
    @ApiModelProperty(value = "answers")
    private List<Answer> answers;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    @ApiModelProperty(value = "user", required = true)
    private Account user;

    @Column(name = "closed")
    private Boolean closed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
