package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.MailParams;
import org.example.entity.AppUser;
import org.example.repository.AppUserRepository;
import org.example.service.AppUserService;
import org.example.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.util.Optional;

import static org.example.entity.enums.UserStatus.BASIC_STATUS;
import static org.example.entity.enums.UserStatus.WAIT_FOR_EMAIL_STATUS;

@RequiredArgsConstructor
@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Override
    public String changeEmail(AppUser appUser) {
        if(appUser.getEmail() != null){
            appUser.setStatus(WAIT_FOR_EMAIL_STATUS);
            appUserRepository.save(appUser);
            return "Введите пожалуйста ваш новый email";
        }
        return "Вы еще не вводили никакого email!";
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегестрированы!";
        } else if(appUser.getEmail() != null){
            return "Вам на почту уже было отправлено письмо! " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        appUser.setStatus(WAIT_FOR_EMAIL_STATUS);
        appUserRepository.save(appUser);
        return "Введите пожалуйста ваш email";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e){
            return "Введите пожалуйста корректный email. Для отмены команды введите /cancel";
        }
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()){
            appUser.setEmail(email);
            appUser.setStatus(BASIC_STATUS);
            appUserRepository.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<String> res = sendRequestToMailService(cryptoUserId, email);
            if (res.getStatusCode() != HttpStatus.OK){
                String msg = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserRepository.save(appUser);
                return msg;
            }
            return "Вам на почту было отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        }else {
            return "Этот email уже используется. Введите корректный email. " +
                    "Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams, httpHeaders);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
