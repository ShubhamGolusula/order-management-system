package com.shubham.repository;

import com.shubham.enums.OrderStatus;
import com.shubham.model.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEvent, Long> {

    @Query(value = """
            with temp as (select o.* , rank() over (partition by order_id order by updated_at desc) as evnt_rnk from orders o)
            select * from temp where evnt_rnk = 1 and status = ?1
        """, nativeQuery = true)
    List<OrderEvent> getOrderByStatus(String status);

    @Query(value = """
            with temp as (select o.* , rank() over (partition by order_id order by updated_at desc) as evnt_rnk from orders o)
            select * from temp where evnt_rnk = 1
        """, nativeQuery = true)
    List<OrderEvent> getAllOrders();

    List<OrderEvent> getByOrderId(String id);
}
