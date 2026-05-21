package com.crm.controller;

import com.crm.dto.request.OpportunityRequest;
import com.crm.dto.response.OpportunityResponse;
import com.crm.service.IOpportunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@RequiredArgsConstructor
@Slf4j
public class OpportunityController {
    
    private final IOpportunityService opportunityService;
    
    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getOpportunityById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }
    
    @PostMapping
    public ResponseEntity<OpportunityResponse> createOpportunity(@RequestBody OpportunityRequest request) {
        OpportunityResponse response = opportunityService.createOpportunity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> updateOpportunity(@PathVariable Long id, @RequestBody OpportunityRequest request) {
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OpportunityResponse>> getOpportunitiesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(opportunityService.getOpportunitiesByCustomerId(customerId));
    }
}
