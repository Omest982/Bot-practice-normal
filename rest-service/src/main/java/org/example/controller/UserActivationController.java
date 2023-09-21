package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserActivationController {
    private final UserActivationService userActivationService;

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        boolean res = userActivationService.activation(id);
        if(res){
            return ResponseEntity.ok().body("Регистрация успешно завершена!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
