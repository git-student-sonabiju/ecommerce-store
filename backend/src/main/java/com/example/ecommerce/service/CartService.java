package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartItemCreationDto;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public Optional<CartDto> getCartByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return getCartByUserId(user.getId());
    }

    public CartDto createCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        return getCartByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found after creation"));
    }

    private Optional<CartDto> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    CartDto cartDto = modelMapper.map(cart, CartDto.class);
                    cartDto.setItems(cart.getCartItems().stream()
                            .map(cartItem -> {
                                CartItemDto itemDto = modelMapper.map(cartItem, CartItemDto.class);
                                // Set product details
                                Product product = cartItem.getProduct();
                                itemDto.setProductId(product.getId());
                                itemDto.setProductName(product.getName());
                                itemDto.setProductImageUrl(product.getImageUrl());
                                itemDto.setPrice(product.getPrice());
                                return itemDto;
                            })
                            .collect(Collectors.toList()));
                    return cartDto;
                });
    }

    public CartDto addItemToCart(String username, CartItemCreationDto itemDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return addItemToCart(user.getId(), itemDto);
    }

    private CartDto addItemToCart(Long userId, CartItemCreationDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Check if the product is already in the cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + itemDto.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(itemDto.getQuantity());
            cartItemRepository.save(newCartItem);
        }

        // Return updated cart
        return getCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found after adding item"));
    }

    public CartDto updateCartItem(String username, Long cartItemId, CartItemCreationDto itemDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return updateCartItem(user.getId(), cartItemId, itemDto);
    }

    private CartDto updateCartItem(Long userId, Long cartItemId, CartItemCreationDto itemDto) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify that the cart item belongs to the user's cart
        if (!cart.getCartItems().contains(cartItem)) {
            throw new ResourceNotFoundException("Cart item does not belong to the user's cart");
        }

        cartItem.setQuantity(itemDto.getQuantity());
        cartItemRepository.save(cartItem);

        // Return updated cart
        return getCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found after updating item"));
    }

    @Transactional
    public void removeItemFromCart(String username, Long cartItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        removeItemFromCart(user.getId(), cartItemId);
    }

    private void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify ownership by ID, then mutate the orphan-removal collection.
        if (cart.getCartItems().stream().noneMatch(item -> item.getId().equals(cartItemId))) {
            throw new ResourceNotFoundException("Cart item does not belong to the user's cart");
        }

        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
    }

    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        clearCart(user.getId());
    }

    private void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        cart.getCartItems().clear();
    }
}
