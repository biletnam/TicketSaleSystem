package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.ErrorResponse;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.service.AccountService;
import ru.tersoft.service.OrderService;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@Api(description = "Work with orders", tags = {"Order"})
public class OrderController {
    @Resource(name = "OrderService")
    private OrderService orderService;
    @Resource(name = "AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "orders", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of orders", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Page<Order> getOrders(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                 @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return orderService.getAll(pageNum, limit);
    }

    @ApiOperation(value = "Get order by id", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("id") UUID id) {
        Order order = orderService.get(id);
        if(order != null) return new ResponseEntity<>(order, HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Order with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Get orders by account id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getByAccount(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        if(account != null) return new ResponseEntity<>((List<Order>)orderService.getByAccount(id), HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Order with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Get orders for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/", method = RequestMethod.GET)
    public List<Order> getByCurrentAccount(Principal principal) {
        UUID id = accountService.findUserByMail(principal.getName()).getId();
        return (List<Order>)orderService.getByAccount(id);
    }

    @RequestMapping(value = "orders", method = RequestMethod.POST)
    @ApiOperation(value = "Create new order", notes = "You don't need to pass account id here", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestBody Order order, Principal principal) {
        if(order != null) {
            order.setAccount(accountService.findUserByMail(principal.getName()));
            Order addedOrder = orderService.add(order);
            return new ResponseEntity<>(addedOrder, HttpStatus.OK);
        } else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty order"),
                        HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Delete order")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Order order = orderService.get(id);
        if(order != null) {
            orderService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Order with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Add tickets to existing order", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}/addtickets", method = RequestMethod.POST)
    public ResponseEntity<?> addTicket(@PathVariable("id") UUID id, @RequestBody List<Ticket> tickets) {
        if(tickets.size() > 0) {
            Boolean isAdded = orderService.addTickets(id, tickets);
            if(isAdded)
                return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
            else return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                            "Order with such id was not found"),
                            HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                            "Passed empty ticket array"),
                            HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Set order as payed", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> setPayed(@PathVariable("id") UUID id) {
        Boolean isSet = orderService.setPayed(id);
        if(isSet) return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Order with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Disable ticket", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> disableTicket(@PathVariable("id") UUID id) {
        Boolean isDisabled = orderService.disableTicket(id);
        if(isDisabled) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Ticket with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Delete ticket", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTicket(@PathVariable("id") UUID id) {
        Order order = orderService.deleteTicket(id);
        if(order != null) return new ResponseEntity<>(order, HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Ticket with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }
}
