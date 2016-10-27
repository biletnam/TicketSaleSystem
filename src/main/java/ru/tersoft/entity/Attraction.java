package ru.tersoft.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "attraction")
@ApiModel(value = "Attraction")
public class Attraction implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name = "name", nullable = false)
    @ApiModelProperty(value = "name", required = true)
    private String name;

    @Column(name = "description", columnDefinition="TEXT", nullable = false)
    @ApiModelProperty(value = "description", required = true)
    private String description;

    @Column(name = "imagepath")
    private String imagepath;

    @Column(name = "smallimagepath")
    private String smallimagepath;

    @Column(name = "maintaince")
    private Boolean maintaince;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public Boolean getMaintaince() {
        return maintaince;
    }

    public void setMaintaince(Boolean maintaince) {
        this.maintaince = maintaince;
    }

    public String getSmallimagepath() {
        return smallimagepath;
    }

    public void setSmallimagepath(String smallimagepath) {
        this.smallimagepath = smallimagepath;
    }
}
