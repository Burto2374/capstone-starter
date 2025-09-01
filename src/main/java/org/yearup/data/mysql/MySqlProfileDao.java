package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId)
    {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                Profile profile = new Profile();
                profile.setUserId(rs.getInt("user_id"));
                profile.setFirstName(rs.getString("first_name"));
                profile.setLastName(rs.getString("last_name"));
                profile.setEmail(rs.getString("email"));
                profile.setPhone(rs.getString("phone"));
                // Add other fields as needed
                return profile;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(Profile profile)
    {
        String sql = "UPDATE profiles SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setString(3, profile.getEmail());
            stmt.setString(4, profile.getPhone());
            stmt.setInt(5, profile.getUserId());
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

}
