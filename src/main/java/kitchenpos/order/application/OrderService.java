package kitchenpos.order.application;

import kitchenpos.order.domain.OrderStatus;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderRequest.OrderLineItemRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderTableRepository orderTableRepository;

    private final MenuRepository menuRepository;

    public OrderService(final OrderRepository orderRepository,
                        final OrderTableRepository orderTableRepository,
                        final MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.menuRepository = menuRepository;
    }

    public OrderResponse create(final OrderRequest request) {
        OrderTable orderTable = toOrderTable(request.getOrderTableId());
        List<OrderLineItem> lineItems = toOrderLineItems(request.getOrderLineItems());
        Order savedOrder = orderRepository.save(Order.of(orderTable, lineItems));
        return OrderResponse.of(savedOrder);
    }

    private OrderTable toOrderTable(final Long orderTableId) {
        return orderTableRepository.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("%d에 해당하는 주문 테이블이 없습니다.", orderTableId)));
    }

    private List<OrderLineItem> toOrderLineItems(final List<OrderLineItemRequest> requests) {
        return requests.stream()
                .map(this::toOrderLineItem)
                .collect(Collectors.toList());
    }

    private OrderLineItem toOrderLineItem(final OrderLineItemRequest request) {
        Long menuId = request.getMenuId();
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("%d에 해당하는 메뉴가 없습니다.", menuId)));
        return new OrderLineItem(menu, request.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        List<Order> orders = orderRepository.findAll();
        return OrderResponse.ofList(orders);
    }

    public OrderResponse changeOrderStatus(final Long orderId, final String value) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("%d에 해당하는 주문이 없습니다.", orderId)));
        order.changeStatus(OrderStatus.valueOf(value));
        return OrderResponse.of(order);
    }
}