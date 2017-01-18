package ru.tersoft.ticketsale.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Order;
import ru.tersoft.ticketsale.entity.Ticket;
import ru.tersoft.ticketsale.repository.AccountRepository;
import ru.tersoft.ticketsale.repository.AttractionRepository;
import ru.tersoft.ticketsale.repository.OrderRepository;
import ru.tersoft.ticketsale.repository.TicketRepository;
import ru.tersoft.ticketsale.service.OrderService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@Service("OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AttractionRepository attractionRepository;
    private final TicketRepository ticketRepository;
    private final AccountRepository accountRepository;

    @Value("${ticketsale.images-folder}")
    private String imagesLocation;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, AttractionRepository attractionRepository, TicketRepository ticketRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.attractionRepository = attractionRepository;
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public ResponseEntity<?> getAll(int pagenum, int limit) {
        return ResponseFactory.createResponse(orderRepository.findAll(new PageRequest(pagenum, limit)));
    }

    @Override
    public ResponseEntity<?> get(UUID id) {
        Order order = orderRepository.findOne(id);
        if(order != null)
            return ResponseFactory.createResponse(order);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
    }

    @Override
    public ResponseEntity<?> getByAccount(UUID accountid) {
        Account account = accountRepository.findOne(accountid);
        if(account != null) {
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.MONTH, -2);
            return ResponseFactory.createResponse(orderRepository.findByAccount(accountRepository.findOne(accountid), minDate.getTime()));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> add(Order order) {
        Order countedOrder = countTotal(order);
        countedOrder = orderRepository.saveAndFlush(countedOrder);
        setOrders(countedOrder, countedOrder.getTickets());
        return ResponseFactory.createResponse(countedOrder);
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        Order order = orderRepository.findOne(id);
        if(order != null) {
            for(Ticket ticket : order.getTickets()) {
                deleteCode(ticket.getId());
                orderRepository.delete(id);
            }
            return ResponseFactory.createResponse();
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> setPayed(UUID orderid) {
        Order order = orderRepository.findOne(orderid);
        if(order != null) {
            order.setPayed(true);
            for(Ticket ticket : order.getTickets()) {
                ticket.setEnabled(true);
                ticket.setCode(generateCode(ticket.getId()));
                ticketRepository.saveAndFlush(ticket);
            }
            return ResponseFactory.createResponse(orderRepository.saveAndFlush(order));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> addTickets(UUID orderid, List<Ticket> tickets) {
        if(tickets.size() > 0) {
            Order order = orderRepository.findOne(orderid);
            if(order != null) {
                setOrders(order, tickets);
                return ResponseFactory.createResponse(orderRepository.saveAndFlush(countTotal(order)));
            } else {
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty ticket array");
        }
    }

    @Override
    public ResponseEntity<?> deleteTicket(UUID ticketid) {
        Ticket ticket = ticketRepository.findOne(ticketid);
        if(ticket != null) {
            deleteCode(ticketid);
            UUID orderid = ticket.getOrder().getId();
            ticketRepository.delete(ticket);
            Order order = orderRepository.findOne(orderid);
            Order savedOrder = orderRepository.saveAndFlush(order);
            return ResponseFactory.createResponse(countTotal(savedOrder));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Ticket with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> disableTicket(UUID ticketid) {
        Ticket existingTicket = ticketRepository.findOne(ticketid);
        if(existingTicket != null) {
            deleteCode(ticketid);
            existingTicket.setCode(null);
            existingTicket.setEnabled(false);
            return ResponseFactory.createResponse(ticketRepository.saveAndFlush(existingTicket));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Ticket with such id was not found");
        }
    }

    private Boolean deleteCode(UUID ticketid) {
        String id = ticketid.toString();
        String filePath = imagesLocation + "qr/" + id + ".png";
        File myFile = new File(filePath);
        return myFile.delete();
    }

    private String generateCode(UUID ticketid) {
        String id = ticketid.toString();
        String filePath = imagesLocation + "qr/" + id + ".png";
        int size = 150;
        String fileType = "png";
        File myFile = new File(filePath);
        try {
            myFile.createNewFile();
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 1);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(id, BarcodeFormat.QR_CODE, size,
                    size, hintMap);
            int width = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(width, width,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, width);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, fileType, myFile);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return "/img/qr/" + id + ".png";
    }

    private Order countTotal(Order order) {
        List<Ticket> tickets = order.getTickets();
        BigDecimal total = BigDecimal.ZERO;
        for (Ticket ticket : tickets) {
            Attraction currAttraction = attractionRepository.findOne(ticket.getAttraction().getId());
            total = total.add(currAttraction.getPrice());
        }
        order.setTotal(total);
        return order;
    }

    private void setOrders(Order order, List<Ticket> tickets) {
        for (int i = 0; i < tickets.size(); i++) { // Avoid ConcurrentModificationException by not using foreach
            Ticket ticket = tickets.get(i);
            ticket.setAttraction(attractionRepository.findOne(ticket.getAttraction().getId()));
            ticket.setOrder(order);
            ticket.setEnabled(false);
            ticketRepository.saveAndFlush(ticket);
        }
    }
}
