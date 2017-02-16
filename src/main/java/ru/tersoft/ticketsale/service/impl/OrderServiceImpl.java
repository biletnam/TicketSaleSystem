package ru.tersoft.ticketsale.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Transactional(rollbackFor=LockAcquisitionException.class)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AttractionRepository attractionRepository;
    private final TicketRepository ticketRepository;

    @Value("${ticketsale.images-folder}")
    private String imagesLocation;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, AttractionRepository attractionRepository, TicketRepository ticketRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.attractionRepository = attractionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public ResponseEntity<?> get(UUID id, String mail) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            if (mail.equals(order.getAccount().getMail()))
                return ResponseFactory.createResponse(order);
            else
                return ResponseFactory.createErrorResponse(HttpStatus.UNAUTHORIZED, "Access denied");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Order with such id was not found");
        }
    }

    private Order createCart(Account account) {
        Order cart = new Order();
        cart.setAccount(account);
        cart.setPayed(false);
        cart.setTickets(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        return orderRepository.saveAndFlush(cart);
    }

    @Override
    public Order getCart(Account account) {
        Order order = orderRepository.findCart(account);
        if (order != null) {
            return order;
        } else {
            return createCart(account);
        }
    }

    @Override
    public ResponseEntity<?> getByAccount(Account account) {
        if(account != null) {
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.MONTH, -2);
            return ResponseFactory.createResponse(orderRepository.findOrders(account, minDate.getTime()));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> finishOrder(Account account, Date visitdate) {
        Order cart = getCart(account);
        if(cart.getTickets().size() == 0)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
        cart.setPayed(true);
        for(Ticket ticket : cart.getTickets()) {
            ticket.setEnabled(true);
            try {
                String code = generateCode(ticket.getId());
                ticket.setCode(code);
            } catch(WriterException | IOException e) {
                return ResponseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            ticketRepository.saveAndFlush(ticket);
        }
        cart.setOrderdate(Calendar.getInstance().getTime());
        cart.setVisitdate(visitdate);
        createCart(account);
        return ResponseFactory.createResponse(orderRepository.saveAndFlush(cart));
    }

    @Override
    public ResponseEntity<?> addTickets(Account account, List<String> attractions) {
        if(attractions.size() > 0) {
            Order cart = getCart(account);
            BigDecimal total = BigDecimal.ZERO;
            List<Ticket> tickets = cart.getTickets();
            List<Ticket> newTickets = new ArrayList<>();
            for(String attraction : attractions) {
                Attraction existingAttraction = attractionRepository.findOne(UUID.fromString(attraction));
                if(existingAttraction == null)
                    return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
                Ticket ticket = new Ticket();
                ticket.setOrder(cart);
                ticket.setAttraction(existingAttraction);
                ticket.setEnabled(false);
                tickets.size();
                newTickets.add(ticketRepository.save(ticket));
            }
            ticketRepository.flush();
            tickets.addAll(newTickets);
            for(Ticket ticket : tickets) {
                total = total.add(ticket.getAttraction().getPrice());
            }
            cart.setTickets(tickets);
            cart.setTotal(total);
            return ResponseFactory.createResponse(orderRepository.saveAndFlush(cart));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty attractions");
        }
    }

    @Override
    public ResponseEntity<?> deleteTickets(Account account, List<String> attractions) {
        if(attractions.size() > 0) {
            Order cart = orderRepository.findCart(account);
            BigDecimal total = BigDecimal.ZERO;
            List<Ticket> tickets = cart.getTickets();
            List<Ticket> deletedTickets = new ArrayList<>();
            for (String attrid : attractions) {
                Attraction attraction = attractionRepository.findOne(UUID.fromString(attrid));
                if (attraction == null)
                    return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
                for (Ticket ticket : tickets) {
                    if(ticket.getAttraction().getId().equals(attraction.getId())) {
                        tickets.remove(ticket);
                        deletedTickets.add(ticket);
                        break;
                    }
                }
            }
            ticketRepository.delete(deletedTickets);
            cart.setTickets(tickets);
            for(Ticket ticket : tickets) {
                total = total.add(ticket.getAttraction().getPrice());
            }
            cart.setTotal(total);
            return ResponseFactory.createResponse(orderRepository.saveAndFlush(cart));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty attractions");
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

    private String generateCode(UUID ticketid) throws WriterException, IOException {
        String id = ticketid.toString();
        String filePath = imagesLocation + "qr/" + id + ".png";
        int size = 150;
        String fileType = "png";
        File myFile = new File(filePath);
        myFile.createNewFile();
        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(id, BarcodeFormat.QR_CODE, size,
                size, hintMap);
        int width = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
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
        return "/img/qr/" + id + ".png";
    }
}
