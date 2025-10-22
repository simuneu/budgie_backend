package com.budgie.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {
//    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    //메일로 인증번호 보내기
//    private MimeMessage createMessage(String to, String pw) throws MessageEx

}
