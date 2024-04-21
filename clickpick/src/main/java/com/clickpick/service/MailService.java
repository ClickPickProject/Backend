package com.clickpick.service;


import com.clickpick.config.RedisUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailService {


    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    private MimeMessage createMessage(String id) throws  Exception{
        String code = createCode();
        MimeMessage  message = javaMailSender.createMimeMessage();
        redisUtil.setDataExpire(code,id,60*5L);
        message.addRecipients(MimeMessage.RecipientType.TO, id);//보내는 대상
        message.setSubject("ClickPick 회원가입 이메일 인증");//제목
        String msgg = getString(code);
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("taemin14215@gmail.com","ClickPick Admin"));//보내는 사람

        return message;
    }

    private static String getString(String Code) {
        String msgg= "";

        msgg+= "<div style='margin:100px;'>";
        msgg+= "<h1> 안녕하세요 ClickPick 관리자입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다!<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= Code +"</strong><div><br/> ";
        msgg+= "</div>";
        return msgg;
    }

    private String createCode() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 10; i++) { // 인증코드 10자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }

    public void sendMessage(String to) throws Exception {
        MimeMessage message = createMessage(to);
        try{//예외처리
            javaMailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
