package io.cojam.web.service;

import io.cojam.web.domain.Mail;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender javaMailSender;
    private static final String FROM_ADDRESS = "support@cojam.io";

    public void mailSend(Mail mailDto) {
        try {
            StringBuilder body = new StringBuilder();
            body.append("<html> <body>");
            body.append(mailDto.getMessage());
            body.append("</body></html>");

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            mimeMessageHelper.setFrom(FROM_ADDRESS);
            mimeMessageHelper.setTo(mailDto.getAddress());
            mimeMessageHelper.setSubject(mailDto.getTitle());
            mimeMessageHelper.setText(body.toString(), true);
            System.out.println(String.format("Mail TO : %s FROM : %s SEND",mailDto.getAddress(),MailService.FROM_ADDRESS));
            javaMailSender.send(message);
            System.out.println(String.format("Mail TO : %s FROM : %s END",mailDto.getAddress(),MailService.FROM_ADDRESS));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
