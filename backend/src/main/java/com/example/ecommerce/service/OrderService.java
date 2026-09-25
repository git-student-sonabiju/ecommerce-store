package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderCreationDto;
import com.example.ecommerce.dto.OrderItemDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public OrderDto createOrder(Long userId, OrderCreationDto orderDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate and prepare order items
        List<OrderItem> orderItems = orderDto.getItems().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDto.getProductId()));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setPrice(product.getPrice()); // Price at the time of purchase
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now(ZoneOffset.UTC));
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);
        order.setOrderItems(orderItems);

        // Set the order for each order item
        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        OrderDto createdOrderDto = modelMapper.map(savedOrder, OrderDto.class);
        createdOrderDto.setOrderNumber(Math.toIntExact(orderRepository.countByUserId(userId)));
        return createdOrderDto;
    }

    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                    List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                            .map(orderItem -> {
                                OrderItemDto itemDto = modelMapper.map(orderItem, OrderItemDto.class);
                                // Set product details
                                Product product = orderItem.getProduct();
                                itemDto.setProductId(product.getId());
                                itemDto.setProductName(product.getName());
                                itemDto.setProductImageUrl(product.getImageUrl());
                                itemDto.setPrice(product.getPrice());
                                return itemDto;
                            })
                            .collect(Collectors.toList());
                    orderDto.setItems(itemDtos);
                    return orderDto;
                });
    }

    @Transactional
    public OrderDto cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only placed or confirmed orders can be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return getOrdersByUserId(userId).stream()
                .filter(userOrder -> userOrder.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        List<Order> userOrders = orderRepository.findByUserIdOrderByIdAsc(userId);
        return java.util.stream.IntStream.range(0, userOrders.size())
                .mapToObj(index -> {
                    Order order = userOrders.get(index);
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                    orderDto.setOrderNumber(index + 1);
                    List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                            .map(orderItem -> {
                                OrderItemDto itemDto = modelMapper.map(orderItem, OrderItemDto.class);
                                // Set product details
                                Product product = orderItem.getProduct();
                                itemDto.setProductId(product.getId());
                                itemDto.setProductName(product.getName());
                                itemDto.setProductImageUrl(product.getImageUrl());
                                itemDto.setPrice(product.getPrice());
                                return itemDto;
                            })
                            .collect(Collectors.toList());
                    orderDto.setItems(itemDtos);
                    return orderDto;
                })
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                    List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                            .map(orderItem -> {
                                OrderItemDto itemDto = modelMapper.map(orderItem, OrderItemDto.class);
                                // Set product details
                                Product product = orderItem.getProduct();
                                itemDto.setProductId(product.getId());
                                itemDto.setProductName(product.getName());
                                itemDto.setProductImageUrl(product.getImageUrl());
                                itemDto.setPrice(product.getPrice());
                                return itemDto;
                            })
                            .collect(Collectors.toList());
                    orderDto.setItems(itemDtos);
                    return orderDto;
                })
                .collect(Collectors.toList());
    }

    public Optional<OrderDto> updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            order.setStatus(orderStatus);
            Order updatedOrder = orderRepository.save(order);
            return Optional.of(modelMapper.map(updatedOrder, OrderDto.class));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid order status: " + status);
        }
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
