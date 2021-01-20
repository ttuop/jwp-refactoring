package kitchenpos.order.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.order.domain.Order;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.order.dto.OrderStatusRequest;
import kitchenpos.order.service.OrderService;
import kitchenpos.order.util.OrderRequestBuilder;
import kitchenpos.order.util.OrderResponseBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;


    @DisplayName("주문을 등록할 수 있다.")
    @Test
    void createOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequestBuilder()
                .withOrderTableId(1L)
                .addOrderLineItem(1L, 1)
                .addOrderLineItem(2L, 1)
                .build();
        OrderResponse orderResponse = new OrderResponseBuilder()
                .withOrderId(1L)
                .withOrderStatus("COOKING")
                .withOrderedTime("2020-11-11")
                .addOrderLineItem(1L, 1)
                .addOrderLineItem(2L, 1)
                .build();
        when(orderService.create(any())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .content(objectMapper.writeValueAsString(orderRequest)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("주문 목록을 가져올 수 있다.")
    @Test
    void findAllOrder() throws Exception {
        List<OrderResponse> orderResponses = Arrays.asList(new OrderResponseBuilder()
                        .withOrderId(1L)
                        .withOrderStatus("COOKING")
                        .withOrderedTime("2020-11-11")
                        .build(),
                new OrderResponseBuilder()
                        .withOrderId(2L)
                        .withOrderStatus("COOKING")
                        .withOrderedTime("2020-11-11")
                        .build());

        when(orderService.list()).thenReturn(orderResponses);
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @DisplayName("주문의 상태값을 나타낼 수 있다.")
    @Test
    void changeOrderStatus() throws Exception {
        OrderResponse orderResponse = new OrderResponseBuilder()
                .withOrderId(1L)
                .withOrderStatus("COOKING")
                .withOrderedTime("2020-11-11")
                .addOrderLineItem(1L, 1)
                .addOrderLineItem(2L, 1)
                .build();
        when(orderService.changeOrderStatus(anyLong(), anyString())).thenReturn(orderResponse);

        mockMvc.perform(put("/api/orders/{orderId}/order-status", 1L)
                .content(objectMapper.writeValueAsString(new OrderStatusRequest(Order.OrderStatus.MEAL.name())))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }
}