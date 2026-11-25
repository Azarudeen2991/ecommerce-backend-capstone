package org.example.evaluations.evaluation.services;

import org.example.evaluations.evaluation.exceptions.OrderNotFoundException;
import org.example.evaluations.evaluation.models.*;
import org.example.evaluations.evaluation.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ItemDetailRepo itemDetailRepo;

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderStateTimeMappingRepo orderStateTimeMappingRepo;

    public Boolean cancelOrder(Long orderId) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if(orderOptional.isEmpty()) {
            throw new OrderNotFoundException("orderId is wrong");
        }

        Order order = orderOptional.get();
        List<ItemDetail> itemDetails = itemDetailRepo.findByOrder(order); //itemDetails is not null
        for(ItemDetail itemDetail : itemDetails) {
            Item item = itemDetail.getItem(); //item is not null
            Inventory inventory = inventoryRepo.findByItem(item).get();
            Double count = inventory.getCount();
            inventory.setCount(count+itemDetail.getQuantity());
            inventoryRepo.save(inventory);
            itemDetailRepo.deleteById(itemDetail.getId());
        }

        OrderStateTimeMapping orderStateTimeMapping = new OrderStateTimeMapping();
        orderStateTimeMapping.setOrderState(OrderState.CANCELLED);
        orderStateTimeMapping.setOrder(order);
        orderStateTimeMappingRepo.save(orderStateTimeMapping);

        Customer customer = order.getCustomer();
        Long cancellationCount = customer.getOrderCancellationCount();
        customer.setOrderCancellationCount(cancellationCount+1);
        customerRepo.save(customer);
        return true;
    }
}
