package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Repository
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashMap<>());

        String sql = "SELECT c.product_id, c.quantity, p.* " +
                     "FROM shopping_cart c " +
                     "JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                Product product = MySqlProductDao.mapRow(rs);
                int quantity = rs.getInt("quantity");
                ShoppingCartItem item = new ShoppingCartItem(product, quantity, 0); // discountPercent = 0
                cart.getItems().put(product.getProductId(), item);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public void addProduct(int userId, int productId, int quantity)
    {
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection())
        {
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, productId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next())
            {
                // Product already in cart, update quantity
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
            }
            else
            {
                // Product not in cart, insert new row
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}