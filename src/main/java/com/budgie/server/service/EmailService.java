package com.budgie.server.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    //메일로 인증번호 보내기
    private MimeMessage createMessage(String to, String number) throws MessagingException {
        log.debug("보내는 대상 : " + to);
        log.debug("인증 번호 : " + number);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[budgie]에서 회원 가입 인증 코드를 보냅니다");

        String msg = String.format(
            """
            <div style="font-family: 'Pretendard', 'Apple SD Gothic Neo', sans-serif;
                        max-width: 480px;
                        margin: 0 auto;
                        border-radius: 16px;
                        border: 1px solid #f4cfd3;
                        background-color: #fff6f8;
                        padding: 24px 32px;">
                <div style="text-align: center; margin-bottom: 24px;">
                    <h2 style="color: #ff88aa; margin: 0;">🐥 Budgie</h2>
                    <p style="color: #555; font-size: 15px; margin-top: 8px;">
                        회원가입 인증 코드를 보내드립니다
                    </p>
                </div>
        
                <div style="background: white; border-radius: 12px; padding: 16px;
                            border: 1px solid #ffe0e6; text-align: center;">
                    <p style="color: #333; font-size: 15px; margin-bottom: 12px;">
                        아래의 인증 코드를 입력해주세요 💌
                    </p>
                    <div style="font-size: 24px; letter-spacing: 4px; color: #ff6f91;
                                font-weight: bold;">
                        %s
                    </div>
                </div>
        
                <p style="font-size: 13px; color: #888; text-align: center; margin-top: 20px;">
                    이 메일은 Budgie 회원가입을 위한 인증 메일입니다.<br/>
                    본인이 요청하지 않았다면 무시하셔도 됩니다.
                </p>
            </div>
            """, number
        );

        //html활성화
        helper.setText(msg, true);

        try{
            helper.setFrom(new InternetAddress(fromEmail, "budbgie"));
        }catch (UnsupportedEncodingException e){
            throw new  RuntimeException("이메일 발신자 설정 중 인코딩 오류", e);
        }catch (MessagingException e){
            throw new RuntimeException("이메일 설정 오류 발생", e);
        }

        return message;
    }

    //이메일로 보낼 인증코드 생성
    public String createVerificationCode(){
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final int CODE_LENGTH = 8;

        SecureRandom random = new SecureRandom();

        String code = random.ints(CODE_LENGTH, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
        return code;
    }

    //이메일 전송
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            // 1. HTML 메일 메시지 생성
            MimeMessage message = createMessage(toEmail, verificationCode);

            // 2. 메일 전송
            mailSender.send(message);
        } catch (MessagingException  e) {
            log.error("인증 이메일 전송 중 오류 발생: {}", e.getMessage());
            // 예외 처리 로직 추가 (예: RuntimeException throw)
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // 비밀번호 재설정 메일 생성
    private MimeMessage createResetMessage(String to, String code) throws MessagingException {
        log.debug("비밀번호 재설정 대상 : {}", to);
        log.debug("재설정 코드 : {}", code);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[Budgie] 비밀번호 재설정 코드 안내");

        String msg = String.format(
                """
                <div style="font-family: 'Pretendard', sans-serif;
                            max-width: 480px;
                            margin: 0 auto;
                            border-radius: 16px;
                            border: 1px solid #d3e7ff;
                            background-color: #f6faff;
                            padding: 24px 32px;">
                    <div style="text-align: center; margin-bottom: 24px;">
                        <h2 style="color: #4d93ff; margin: 0;">🔐 Budgie</h2>
                        <p style="color: #555; font-size: 15px; margin-top: 8px;">
                            비밀번호 재설정 코드를 보내드립니다
                        </p>
                    </div>
        
                    <div style="background: white; border-radius: 12px; padding: 16px;
                                border: 1px solid #d3e7ff; text-align: center;">
                        <p style="color: #333; font-size: 15px; margin-bottom: 12px;">
                            아래의 8자리 코드를 입력해 비밀번호를 재설정하세요.
                        </p>
                        <div style="font-size: 24px; letter-spacing: 4px; color: #4d77ff;
                                    font-weight: bold;">
                            %s
                        </div>
                    </div>
        
                    <p style="font-size: 13px; color: #888; text-align: center; margin-top: 20px;">
                        이 메일은 비밀번호 재설정을 요청한 사용자에게 발송되었습니다.<br/>
                        본인이 요청하지 않았다면 무시하셔도 됩니다.
                    </p>
                </div>
                """, code
        );

        helper.setText(msg, true);

        try {
            helper.setFrom(new InternetAddress(fromEmail, "Budgie"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 발신자 설정 중 오류", e);
        }

        return message;
    }

    //이메일 전송
    public void sendPasswordResetEmail(String toEmail, String code){
        try{
            MimeMessage message = createResetMessage(toEmail, code);
            mailSender.send(message);
        }catch (MessagingException e){
            log.error("비밀번호 재설정 이메일 전송 오류:{}" ,e.getMessage());
            throw new RuntimeException("비밀번호 재설정 메일 전송 실패");
        }
    }
}
