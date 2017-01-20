package ru.tersoft.ticketsale.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@ApiModel(value = "Order")
public class Order implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account", nullable = false)
    @ApiModelProperty(required = true)
    private Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ApiModelProperty(required = true)
    private List<Ticket> tickets;

    @Column(name = "orderdate", columnDefinition = "DATE")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date orderdate;

    @Column(name = "visitdate", columnDefinition = "DATE")
    @ApiModelProperty(required = true, example = "1970-01-01")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date visitdate;

    @Column(name = "total", columnDefinition = "decimal(19,4)")
    @ApiModelProperty(example = "999.99")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;

    @Column(name = "payed")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean payed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Boolean isPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getVisitdate() {
        return visitdate;
    }

    public void setVisitdate(Date visitdate) {
        this.visitdate = visitdate;
    }
}
