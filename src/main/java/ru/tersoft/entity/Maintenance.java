package ru.tersoft.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "maintaince")
@ApiModel(value = "Maintenance")
public class Maintenance implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "attractionid", nullable = false)
    @ApiModelProperty(value = "attractionid", required = true)
    private Attraction attraction;

    @Column(name = "startdate", nullable = false)
    @ApiModelProperty(value = "startdate", required = true)
    private Date startdate;

    @Column(name = "enddate", nullable = false)
    @ApiModelProperty(value = "enddate", required = true)
    private Date enddate;

    @Column(name = "reason", columnDefinition="TEXT", nullable = false)
    @ApiModelProperty(value = "reason", required = true)
    private String reason;

    public Attraction getAttractionid() {
        return attraction;
    }

    public void setAttractionid(Attraction attractionid) {
        this.attraction = attractionid;
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
