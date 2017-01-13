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
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.service.AccountService;
import ru.tersoft.service.OrderService;
import ru.tersoft.utils.ResponseFactory;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/")
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
        if(order != null)
            return ResponseFactory.createResponse(order);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Get orders by account id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getByAccount(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        if(account != null)
            return ResponseFactory.createResponse(orderService.getByAccount(id));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
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
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        if(order != null) {
            order.setAccount(accountService.findUserByMail(principal.getName()));
            Order addedOrder = orderService.add(order);
            return ResponseFactory.createResponse(addedOrder);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty order");
        }
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
            return ResponseFactory.createResponse();
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
        }
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
                return ResponseFactory.createResponse(orderService.get(id));
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty ticket array");
        }
    }

    @ApiOperation(value = "Set order as payed", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> setPayed(@PathVariable("id") UUID id) {
        Boolean isSet = orderService.setPayed(id);
        if(isSet)
            return ResponseFactory.createResponse(orderService.get(id));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
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
            return ResponseFactory.createResponse();
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Ticket with such id was not found");
        }
    }

    @ApiOperation(value = "Delete ticket", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTicket(@PathVariable("id") UUID id) {
        Order order = orderService.deleteTicket(id);
        if(order != null)
            return ResponseFactory.createResponse(order);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Ticket with such id was not found");
    }
}
