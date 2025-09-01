package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao
{
    public MySqlOrderDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Order createOrderFromCart(int userId, ShoppingCart cart)
    {
        String insertOrderSql = "INSERT INTO orders (user_id, order_date, total) VALUES (?, ?, ?)";
        String insertLineItemSql = "INSERT INTO order_line_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        Order order = new Order();
        List<OrderLineItem> lineItems = new ArrayList<>();

        try (Connection conn = getConnection())
        {
            // 1. Insert order
            PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.setBigDecimal(3, cart.getTotal());
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next())
            {
                orderId = generatedKeys.getInt(1);
            }

            // 2. Insert order line items
            for (ShoppingCartItem cartItem : cart.getItems().values())
            {
                PreparedStatement lineItemStmt = conn.prepareStatement(insertLineItemSql);
                lineItemStmt.setInt(1, orderId);
                lineItemStmt.setInt(2, cartItem.getProduct().getProductId());
                lineItemStmt.setInt(3, cartItem.getQuantity());
                lineItemStmt.setBigDecimal(4, cartItem.getProduct().getPrice());
                lineItemStmt.executeUpdate();

                // Build OrderLineItem for return object
                OrderLineItem oli = new OrderLineItem();
                oli.setOrderId(orderId);
                oli.setProductId(cartItem.getProduct().getProductId());
                oli.setQuantity(cartItem.getQuantity());
                oli.setPrice(cartItem.getProduct().getPrice());
                lineItems.add(oli);
            }

            // 3. Build and return the Order object
            order.setOrderId(orderId);
            order.setUserId(userId);
            order.setOrderDate(LocalDateTime.now());
            order.setTotal(cart.getTotal());
            order.setLineItems(lineItems);

            return order;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
