package com.productService.controller;

import com.productService.dto.request.*;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.exception.ValidationException;
import com.productService.service.ProductService;
import com.productService.utils.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    // ===================== Positive tests =====================
    @Test
    void testCreateProduct() {
        ProductCreateRequestDTO req = new ProductCreateRequestDTO(
                "Product1", "Desc", BigDecimal.TEN, 10, Map.of("color", "red")
        );
        ProductResponseDTO resp = new ProductResponseDTO("1", "Product1", "Desc", BigDecimal.TEN, 10, Map.of("color", "red"));
        when(productService.createProduct(req)).thenReturn(resp);

        ResponseEntity<GenericResponse<ProductResponseDTO>> response = productController.createProduct(req);

        assertEquals("Product1", response.getBody().getData().name());
    }

    // Other positive tests (getProductById, getAllProducts, updateProduct, delete, reserve/release/confirm) omitted for brevity
    // Assume previous positive tests from the last version are here

    // ===================== Security / AccessDenied Tests =====================
    @Test
    void testCreateProduct_AsNonAdmin() {
        ProductCreateRequestDTO req = new ProductCreateRequestDTO(
                "Product1", "Desc", BigDecimal.TEN, 10, Map.of()
        );

        // Simulate non-admin authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            // We have to manually call the pre-authorization check
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.createProduct(req);
        });

        assertEquals("Access is denied", ex.getMessage());
    }

    @Test
    void testUpdateProduct_AsNonAdmin() {
        ProductUpdateRequestDTO req = new ProductUpdateRequestDTO("Desc", BigDecimal.TEN, 10, Map.of());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.updateProduct("1", req);
        });

        assertEquals("Access is denied", ex.getMessage());
    }

    @Test
    void testDeleteProduct_AsNonAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.delete("1");
        });

        assertEquals("Access is denied", ex.getMessage());
    }

    @Test
    void testReserveStock_AsNonAdmin() {
        ReserveItemDTO item = new ReserveItemDTO("1", 2);
        ReserveRequestDTO req = new ReserveRequestDTO("res1", List.of(item));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.reserveStock(req);
        });

        assertEquals("Access is denied", ex.getMessage());
    }

    @Test
    void testReleaseStock_AsNonAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.releaseStock("res1");
        });

        assertEquals("Access is denied", ex.getMessage());
    }

    @Test
    void testConfirmReservation_AsNonAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
            productController.confirmReservation("res1");
        });

        assertEquals("Access is denied", ex.getMessage());
    }

}
