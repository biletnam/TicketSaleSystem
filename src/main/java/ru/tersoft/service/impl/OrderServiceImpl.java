package ru.tersoft.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.repository.OrderRepository;
import ru.tersoft.repository.TicketRepository;
import ru.tersoft.service.OrderService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public Page<Order> getAll(int pagenum, int limit) {
        return orderRepository.findAll(new PageRequest(pagenum, limit));
    }

    @Override
    public Order get(UUID id) {
        return orderRepository.findOne(id);
    }

    @Override
    public Iterable<Order> getByAccount(UUID accountid) {
        return orderRepository.findByAccount(accountRepository.findOne(accountid));
    }

    @Override
    public Order add(Order order) {
        Order countedOrder = countTotal(order);
        countedOrder = orderRepository.saveAndFlush(countedOrder);
        setOrders(countedOrder, countedOrder.getTickets());
        return countedOrder;
    }

    @Override
    public Boolean delete(UUID id) {
        if(orderRepository.findOne(id) != null) {
            for(Ticket ticket : orderRepository.findOne(id).getTickets()) {
                deleteCode(ticket.getId());
            }
            orderRepository.delete(id);
            return true;
        } else return false;
    }

    @Override
    public Boolean setPayed(UUID orderid) {
        Order order = orderRepository.findOne(orderid);
        if(order != null) {
            order.setPayed(true);
            for(Ticket ticket : order.getTickets()) {
                ticket.setEnabled(true);
                ticket.setCode(generateCode(ticket.getId()));
                ticketRepository.saveAndFlush(ticket);
            }
            orderRepository.saveAndFlush(order);
            return true;
        } else return false;
    }

    @Override
    public Boolean addTickets(UUID orderid, List<Ticket> tickets) {
        Order order = orderRepository.findOne(orderid);
        if(order != null) {
            setOrders(order, tickets);
            orderRepository.saveAndFlush(countTotal(order));
            return true;
        } else return false;
    }

    @Override
    public Order deleteTicket(UUID ticketid) {
        Ticket ticket = ticketRepository.findOne(ticketid);
        if(ticket != null) {
            deleteCode(ticketid);
            UUID orderid = ticket.getOrder().getId();
            ticketRepository.delete(ticket);
            Order order = orderRepository.findOne(orderid);
            Order savedOrder = orderRepository.saveAndFlush(order);
            return countTotal(savedOrder);
        } else return null;
    }

    @Override
    public Boolean disableTicket(UUID ticketid) {
        Ticket existingTicket = ticketRepository.findOne(ticketid);
        if(existingTicket != null) {
            deleteCode(ticketid);
            existingTicket.setCode(null);
            existingTicket.setEnabled(false);
            ticketRepository.saveAndFlush(existingTicket);
            return true;
        }
        else return false;
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
        return "/images/qr/" + id + ".png";
    }

    private Order countTotal(Order order) {
        List<Ticket> tickets = order.getTickets();
        Float total = 0F;
        for (Ticket ticket : tickets) {
            Attraction currAttraction = attractionRepository.findOne(ticket.getAttraction().getId());
            total += currAttraction.getPrice();
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
