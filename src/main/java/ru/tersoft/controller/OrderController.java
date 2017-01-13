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
    public ResponseEntity<?> getOrders(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                 @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return orderService.getAll(pageNum, limit);
    }

    @ApiOperation(value = "Get order by id", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("id") UUID id) {
        return orderService.get(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Get orders by account id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getByAccount(@PathVariable("id") UUID id) {
        return orderService.getByAccount(id);
    }

    @ApiOperation(value = "Get orders for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/user/", method = RequestMethod.GET)
    public ResponseEntity<?> getByCurrentAccount(Principal principal) {
        UUID id = accountService.findUserByMail(principal.getName()).getId();
        return orderService.getByAccount(id);
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
            return orderService.add(order);
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
        return orderService.delete(id);
    }

    @ApiOperation(value = "Add tickets to existing order", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}/addtickets", method = RequestMethod.POST)
    public ResponseEntity<?> addTicket(@PathVariable("id") UUID id, @RequestBody List<Ticket> tickets) {
        return orderService.addTickets(id, tickets);
    }

    @ApiOperation(value = "Set order as payed", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> setPayed(@PathVariable("id") UUID id) {
        return orderService.setPayed(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Disable ticket", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> disableTicket(@PathVariable("id") UUID id) {
        return orderService.disableTicket(id);
    }

    @ApiOperation(value = "Delete ticket", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTicket(@PathVariable("id") UUID id) {
        return orderService.deleteTicket(id);
    }
}
