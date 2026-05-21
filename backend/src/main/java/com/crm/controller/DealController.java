package com.crm.controller;

import com.crm.dto.request.DealRequest;
import com.crm.dto.response.DealResponse;
import com.crm.service.IDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Slf4j
public class DealController {
    
    private final IDealService dealService;
    
    @GetMapping
    public ResponseEntity<List<DealResponse>> getAllDeals() {
        return ResponseEntity.ok(dealService.getAllDeals());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DealResponse> getDealById(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getDealById(id));
    }
    
    @PostMapping
    public ResponseEntity<DealResponse> createDeal(@RequestBody DealRequest request) {
        DealResponse response = dealService.createDeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DealResponse> updateDeal(@PathVariable Long id, @RequestBody DealRequest request) {
        return ResponseEntity.ok(dealService.updateDeal(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeal(@PathVariable Long id) {
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DealResponse>> getDealsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(dealService.getDealsByCustomerId(customerId));
    }
}
