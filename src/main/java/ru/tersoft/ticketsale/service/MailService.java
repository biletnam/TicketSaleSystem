package ru.tersoft.ticketsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.tersoft.ticketsale.entity.Account;

import java.util.Date;

/**
 * Project ticketsale.
 * Created by ivyanni on 14.02.2017.
 */
@Service("MailService")
@Transactional
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String author;

    @Autowired
    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendRegistrationMail(Account account) {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                message.setTo(account.getMail());
                message.setFrom(author);
                message.setSubject("Mail confirmation");
                message.setSentDate(new Date());
                Context context = new Context();
                context.setVariable("mail", account.getMail());
                context.setVariable("activationid", account.getActivationId().toString());
                message.setText(templateEngine.process("email/registration", context), true);
            };
            mailSender.send(preparator);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendNewPasswordMail(String mail, String newPassword) {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                message.setTo(mail);
                message.setFrom(author);
                message.setSubject("New password for Ticketsale");
                message.setSentDate(new Date());
                Context context = new Context();
                context.setVariable("newpass", newPassword);
                message.setText(templateEngine.process("email/newpass", context), true);
            };
            mailSender.send(preparator);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
