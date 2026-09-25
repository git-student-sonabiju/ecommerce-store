package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartItemCreationDto;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    public CartController(CartService cartService, JwtUtil jwtUtil) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = jwtUtil.extractUsername(jwt);
        return cartService.getCartByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(cartService.createCart(username)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(@RequestHeader("Authorization") String token, @RequestBody CartItemCreationDto itemDto) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = jwtUtil.extractUsername(jwt);
        return ResponseEntity.ok(cartService.addItemToCart(username, itemDto));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> updateCartItem(@RequestHeader("Authorization") String token, @PathVariable Long cartItemId, @RequestBody CartItemCreationDto itemDto) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = jwtUtil.extractUsername(jwt);
        return ResponseEntity.ok(cartService.updateCartItem(username, cartItemId, itemDto));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@RequestHeader("Authorization") String token, @PathVariable Long cartItemId) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = jwtUtil.extractUsername(jwt);
        cartService.removeItemFromCart(username, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer "
        String username = jwtUtil.extractUsername(jwt);
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
