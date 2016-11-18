package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.service.OrderService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@Api(description = "Work with orders", tags = {"Order"})
public class OrderController {
    @Resource(name = "OrderService")
    private OrderService orderService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "orders", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of orders", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public List<Order> getOrders() {
        return (List<Order>)orderService.getAll();
    }

    @ApiOperation(value = "Get order by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<Order> get(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Get order by account id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/{id}", method = RequestMethod.GET)
    public List<Order> getByAccount(@PathVariable("id") UUID id) {
        return (List<Order>)orderService.getByAccount(id);
    }

    @RequestMapping(value = "orders", method = RequestMethod.POST)
    @ApiOperation(value = "Create new order")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Order> add(@RequestBody Order order) {
        if(order != null) {
            Order addedOrder = orderService.add(order);
            return new ResponseEntity<>(addedOrder, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Delete order")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        orderService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Add tickets to existing order")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/add/{orderid}", method = RequestMethod.POST)
    public ResponseEntity<Order> addTicket(@PathVariable("orderid") UUID id, @RequestBody List<Ticket> tickets) {
        orderService.addTickets(id, tickets);
        return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Set order as payed")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Order> setPayed(@PathVariable("id") UUID id) {
        orderService.setPayed(id);
        return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete ticket")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTicket(@PathVariable("id") UUID id) {
        orderService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
