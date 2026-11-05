package com.tunombre.tienda.controller;

import com.tunombre.tienda.dto.HttpGlobalResponseDTO;
import com.tunombre.tienda.dto.OrderCreateRequestDTO;
import com.tunombre.tienda.dto.OrderResponseDTO;
import com.tunombre.tienda.dto.OrderStatusUpdateRequestDTO;
import com.tunombre.tienda.enums.RoleEnum;
import com.tunombre.tienda.security.RequiresRole;
import com.tunombre.tienda.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO dto,
            HttpServletRequest request) {

        Long clientId = (Long) request.getAttribute("userId");
        OrderResponseDTO created = ordersService.createOrder(dto, clientId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HttpGlobalResponseDTO.<OrderResponseDTO>builder()
                        .message("Pedido creado exitosamente")
                        .data(created)
                        .build());
    }

    @GetMapping("/mine")
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getMyOrders(HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        List<OrderResponseDTO> orders = ordersService.getMyOrders(clientId);

        return ResponseEntity.ok(HttpGlobalResponseDTO.<List<OrderResponseDTO>>builder()
                .message("Mis pedidos obtenidos correctamente")
                .data(orders)
                .build());
    }

    @GetMapping("/sales")
    @RequiresRole(RoleEnum.VENDEDOR)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getSalesOrders(HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        List<OrderResponseDTO> orders = ordersService.getSalesOrders(sellerId);

        return ResponseEntity.ok(HttpGlobalResponseDTO.<List<OrderResponseDTO>>builder()
                .message("Ventas obtenidas correctamente")
                .data(orders)
                .build());
    }

    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orders = ordersService.getAllOrders();

        return ResponseEntity.ok(HttpGlobalResponseDTO.<List<OrderResponseDTO>>builder()
                .message("Todos los pedidos obtenidos correctamente")
                .data(orders)
                .build());
    }

    @PatchMapping("/{id}/status")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDTO dto,
            HttpServletRequest request) {

        Long currentUserId = (Long) request.getAttribute("userId");
        Integer currentRolId = (Integer) request.getAttribute("rolId");

        OrderResponseDTO updated = ordersService.updateStatus(id, dto.getStatus(), currentUserId, currentRolId);

        return ResponseEntity.ok(HttpGlobalResponseDTO.<OrderResponseDTO>builder()
                .message("Estado del pedido actualizado correctamente")
                .data(updated)
                .build());
    }

    @PatchMapping("/{id}/cancel")
    @RequiresRole(RoleEnum.CLIENTE)
    public ResponseEntity<HttpGlobalResponseDTO<OrderResponseDTO>> cancelOrder(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long currentClientId = (Long) request.getAttribute("userId");
        OrderResponseDTO cancelled = ordersService.cancelOrder(id, currentClientId);

        return ResponseEntity.ok(HttpGlobalResponseDTO.<OrderResponseDTO>builder()
                .message("Pedido cancelado exitosamente y stock reincorporado")
                .data(cancelled)
                .build());
    }
}
