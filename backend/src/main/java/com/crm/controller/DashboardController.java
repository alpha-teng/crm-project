package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ICustomerService customerService;
    private final ILeadService leadService;
    private final IOpportunityService opportunityService;
    private final IDealService dealService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long customerCount = customerService.count();
        long leadCount = leadService.count();
        long opportunityCount = opportunityService.count();
        long dealCount = dealService.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customerCount);
        stats.put("totalLeads", leadCount);
        stats.put("totalOpportunities", opportunityCount);
        stats.put("totalDeals", dealCount);
        stats.put("totalRevenue", 0);
        return ResponseEntity.ok(stats);
    }
}
