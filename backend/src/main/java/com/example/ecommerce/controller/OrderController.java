package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderCreationDto;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtUtil;
import com.example.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestHeader("Authorization") String authorization,
            @RequestBody OrderCreationDto orderDto) {
        String username = jwtUtil.extractUsername(authorization.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(orderService.createOrder(user.getId(), orderDto));
    }

    @GetMapping("/mine")
    public ResponseEntity<java.util.List<OrderDto>> getMyOrders(
            @RequestHeader("Authorization") String authorization) {
        String username = jwtUtil.extractUsername(authorization.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelMyOrder(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long orderId) {
        String username = jwtUtil.extractUsername(authorization.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(orderService.cancelOrder(user.getId(), orderId));
    }
}
