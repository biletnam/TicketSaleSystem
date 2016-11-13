package ru.tersoft.entity;

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
@Table(name = "maintenance")
@ApiModel(value = "Maintenance")
public class Maintenance implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "attraction", nullable = false)
    @ApiModelProperty(value = "attraction", required = true)
    private Attraction attraction;

    @Column(name = "startdate", nullable = false)
    @ApiModelProperty(value = "startdate", required = true)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date startdate;

    @Column(name = "enddate", nullable = false)
    @ApiModelProperty(value = "enddate", required = true)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date enddate;

    @Column(name = "reason", columnDefinition="TEXT", nullable = false)
    @ApiModelProperty(value = "reason", required = true)
    private String reason;

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
