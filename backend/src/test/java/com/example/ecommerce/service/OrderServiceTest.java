package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderCreationDto;
import com.example.ecommerce.dto.OrderItemDto;
import com.example.ecommerce.dto.OrderItemCreationDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderCreationDto orderDto;
    private User user;
    private Product product;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.0"));

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("10.0"));

        OrderItemCreationDto item = new OrderItemCreationDto();
        item.setProductId(1L);
        item.setQuantity(2);
        orderDto = new OrderCreationDto();
        orderDto.setItems(List.of(item));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("20.0"));
        order.setStatus(OrderStatus.PLACED);
        order.setOrderItems(List.of(orderItem));
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());

        // Act
        OrderDto result = orderService.createOrder(1L, orderDto);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("20.0"), result.getTotalAmount());
        assertEquals(OrderStatus.PLACED, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(1L, orderDto);
        });
        verify(orderRepository, never()).save(any(com.example.ecommerce.entity.Order.class));
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(1L, orderDto);
        });
        verify(orderRepository, never()).save(any(com.example.ecommerce.entity.Order.class));
    }

    @Test
    void testGetOrderById_Found() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto() {{
            setItems(List.of(new OrderItemDto() {{
                setProductId(1L);
                setQuantity(2);
                setPrice(new BigDecimal("10.0"));
            }}));
        }});

        // Act
        Optional<OrderDto> result = orderService.getOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1, result.get().getItems().size());
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<OrderDto> result = orderService.getOrderById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetOrdersByUserId() {
        // Arrange
        when(orderRepository.findByUserId(anyLong())).thenReturn(List.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto() {{
            setItems(List.of(new OrderItemDto() {{
                setProductId(1L);
                setQuantity(2);
                setPrice(new BigDecimal("10.0"));
            }}));
        }});

        // Act
        List<OrderDto> result = orderService.getOrdersByUserId(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto() {{
            setItems(List.of(new OrderItemDto() {{
                setProductId(1L);
                setQuantity(2);
                setPrice(new BigDecimal("10.0"));
            }}));
        }});

        // Act
        List<OrderDto> result = orderService.getAllOrders();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());

        // Act
        Optional<OrderDto> result = orderService.updateOrderStatus(1L, "CONFIRMED");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.CONFIRMED, result.get().getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.updateOrderStatus(1L, "CONFIRMED");
        });
        verify(orderRepository, never()).save(any(com.example.ecommerce.entity.Order.class));
    }

    @Test
    void testUpdateOrderStatus_InvalidStatus() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.updateOrderStatus(1L, "INVALID");
        });
        verify(orderRepository, never()).save(any(com.example.ecommerce.entity.Order.class));
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        doNothing().when(orderRepository).deleteById(anyLong());

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }
}