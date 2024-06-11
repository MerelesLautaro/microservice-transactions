package com.lautadev.microservice_transactions.repository;

import com.lautadev.microservice_transactions.dto.AccountDTO;
import com.lautadev.microservice_transactions.dto.UpdateBalanceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservice-account")
public interface IAccountAPIClient {
    @GetMapping("/api/account/get/{id}")
    public AccountDTO findAccount(@PathVariable ("id") Long id);

    @GetMapping("/api/account/get/accountAndUser/{id}")
    public AccountDTO findAccountAndUser(@PathVariable ("id") Long id);

    @PutMapping("/api/account/updateBalance/{id}")
    public AccountDTO updateBalance(@PathVariable ("id") Long id, @RequestBody UpdateBalanceDTO updateBalanceDTO);
}
