package ru.tersoft.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "questions")
@ApiModel(value = "Question")
public class Question implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "answer")
    @ApiModelProperty(value = "answer")
    private Answer answer;

    @Column(name = "date", nullable = false)
    @ApiModelProperty(value = "date", required = true)
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "text", columnDefinition="TEXT", nullable = false)
    @ApiModelProperty(value = "text", required = true)
    private String text;

    @ManyToOne (cascade=CascadeType.ALL)
    @JsonIgnore
    private Dialog dialog;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Answer getAnswer() {
        return answer;
    }

    @JsonIgnore
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }
}
