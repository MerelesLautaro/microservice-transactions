package com.lautadev.microservice_transactions.repository;

import com.lautadev.microservice_transactions.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-account")
public interface IAccountAPIClient {
    @GetMapping("/api/account/get/{id}")
    public AccountDTO findAccount(@PathVariable ("id") Long id);
}
