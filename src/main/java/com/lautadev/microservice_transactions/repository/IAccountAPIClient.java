package com.lautadev.microservice_transactions.repository;

import com.lautadev.microservice_transactions.dto.AccountDTO;
import com.lautadev.microservice_transactions.dto.UpdateBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "microservice-account")
public interface IAccountAPIClient {
    @GetMapping("/api/account/get/{id}")
    public AccountDTO findAccount(@PathVariable ("id") Long id);

    @GetMapping("/api/account/get/accountAndUser/{id}")
    public AccountDTO findAccountAndUser(@PathVariable ("id") Long id);

    @PostMapping("/api/account/updateBalance/{id}")
    void updateBalance(@PathVariable("id") Long id, @RequestBody UpdateBalanceDTO updateBalanceDTO,
                       @RequestHeader("X-HTTP-Method-Override") String methodOverride);
}
