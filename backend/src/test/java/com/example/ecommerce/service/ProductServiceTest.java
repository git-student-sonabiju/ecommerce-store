package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductCreationDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private ProductCreationDto productDto;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        productDto = new ProductCreationDto();
        productDto.setName("Test Product");
        productDto.setDescription("A test product");
        productDto.setPrice(new BigDecimal("10.0"));
        productDto.setStockQuantity(100);
        productDto.setCategoryId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("A test product");
        product.setPrice(new BigDecimal("10.0"));
        product.setStockQuantity(100);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        // Configure modelMapper to map ProductCreationDto to Product and Product to ProductDto
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> destClass = invocation.getArgument(1);
            if (source instanceof ProductCreationDto && destClass == Product.class) {
                ProductCreationDto dto = (ProductCreationDto) source;
                Product product = new Product();
                product.setName(dto.getName());
                product.setDescription(dto.getDescription());
                product.setPrice(dto.getPrice());
                product.setStockQuantity(dto.getStockQuantity());
                return product;
            } else if (source instanceof Product && destClass == ProductDto.class) {
                Product product = (Product) source;
                ProductDto dto = new ProductDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setStockQuantity(product.getStockQuantity());
                return dto;
            }
            return null;
        });
    }

    @Test
    void testCreateProduct_Success() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto result = productService.createProduct(productDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals(100, result.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_CategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(productDto);
        });
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProductById_Found() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Act
        Optional<ProductDto> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<ProductDto> result = productService.getProductById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));

        // Act
        List<ProductDto> result = productService.getAllProducts();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testGetProductsByCategory() {
        // Arrange
        when(productRepository.findByCategoryId(anyLong())).thenReturn(List.of(product));

        // Act
        List<ProductDto> result = productService.getProductsByCategory(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testSearchProducts() {
        // Arrange
        when(productRepository.searchByKeyword(anyString())).thenReturn(List.of(product));

        // Act
        List<ProductDto> result = productService.searchProducts("test");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // Act
        ProductDto result = productService.updateProduct(1L, productDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals(100, result.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(1L, productDto);
        });
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_CategoryNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(1L, productDto);
        });
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        // Arrange
        doNothing().when(productRepository).deleteById(anyLong());

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }
}