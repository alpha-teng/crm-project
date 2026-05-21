package com.crm.controller;

import com.crm.dto.request.FollowUpRequest;
import com.crm.dto.response.FollowUpResponse;
import com.crm.service.IFollowUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
@RequiredArgsConstructor
@Slf4j
public class FollowUpController {
    
    private final IFollowUpService followUpService;
    
    @GetMapping
    public ResponseEntity<List<FollowUpResponse>> getAllFollowUps() {
        return ResponseEntity.ok(followUpService.getAllFollowUps());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FollowUpResponse> getFollowUpById(@PathVariable Long id) {
        return ResponseEntity.ok(followUpService.getFollowUpById(id));
    }
    
    @PostMapping
    public ResponseEntity<FollowUpResponse> createFollowUp(@RequestBody FollowUpRequest request) {
        FollowUpResponse response = followUpService.createFollowUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FollowUpResponse> updateFollowUp(@PathVariable Long id, @RequestBody FollowUpRequest request) {
        return ResponseEntity.ok(followUpService.updateFollowUp(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowUp(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<FollowUpResponse>> getFollowUpsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(followUpService.getFollowUpsByCustomerId(customerId));
    }
}
