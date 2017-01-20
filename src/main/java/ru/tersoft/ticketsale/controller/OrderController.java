package ru.tersoft.ticketsale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.ticketsale.entity.Order;
import ru.tersoft.ticketsale.entity.Ticket;
import ru.tersoft.ticketsale.service.AccountService;
import ru.tersoft.ticketsale.service.OrderService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Date;
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

    @ApiOperation(value = "Get order by id", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("id") UUID id, Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.get(id, principal.getName());
    }

    @ApiOperation(value = "Get user's cart", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders/cart", method = RequestMethod.GET)
    public ResponseEntity<?> getCart(Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.getCart(accountService.findUserByMail(principal.getName()));
    }

    @ApiOperation(value = "Get user's orders")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders", method = RequestMethod.GET)
    public ResponseEntity<?> getByCurrentAccount(Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.getByAccount(accountService.findUserByMail(principal.getName()));
    }

    @ApiOperation(value = "Add tickets to cart", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "tickets", method = RequestMethod.POST)
    public ResponseEntity<?> addTicket(Principal principal, @RequestBody List<Ticket> tickets) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.addTickets(accountService.findUserByMail(principal.getName()), tickets);
    }

    @ApiOperation(value = "Move order from Cart to Orders after completed payment", response = Order.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "orders", method = RequestMethod.PUT)
    public ResponseEntity<?> setPayed(Principal principal, @RequestParam Date visitdate) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.finishOrder(accountService.findUserByMail(principal.getName()), visitdate);
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
    public ResponseEntity<?> deleteTicket(@PathVariable("id") UUID id, Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        else
            return orderService.deleteTicket(id, accountService.findUserByMail(principal.getName()));
    }
}
