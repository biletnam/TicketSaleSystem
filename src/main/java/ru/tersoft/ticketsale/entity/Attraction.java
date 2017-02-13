package ru.tersoft.ticketsale.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.util.HtmlUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "attractions")
@ApiModel(value = "Attraction")
public class Attraction implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name = "name", nullable = false)
    @ApiModelProperty(required = true)
    private String name;

    @Column(name = "description", columnDefinition="TEXT", nullable = false)
    @ApiModelProperty(required = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @Column(name = "price", columnDefinition = "decimal(19,4)", nullable = false)
    @ApiModelProperty(required = true)
    private BigDecimal price;

    @Column(name = "image")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String image;

    @Column(name = "thumbnail")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String thumbnail;

    @OneToOne
    private Maintenance maintenance;

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
        this.name = HtmlUtils.htmlEscape(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = HtmlUtils.htmlEscape(description);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }
}
