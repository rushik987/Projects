package ca.sheridancollege.bhavsvir.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.bhavsvir.beans.Review;
import ca.sheridancollege.bhavsvir.beans.User;

@Repository
public class DatabaseAccess {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Retrieve a user by email (email is the unique identifier now)
    public User findUserAccount(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM sec_user where email = :email";
		namedParameters.addValue("email", email);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<User>(User.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

    // Retrieve all reviews along with the username
    public List<Review> getAllReviews() {
        String query = "SELECT r.id, r.book_title, r.review_text, r.user_id, u.email AS username " +
                       "FROM reviews r INNER JOIN sec_user u ON r.user_id = u.userId";
        return jdbc.query(query, new BeanPropertyRowMapper<>(Review.class));
    }

    // Add a new user (with email, password, and role)
    public void addUser(String email, String password) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO sec_user " + "(email, encryptedPassword, enabled) "
				+ "VALUES (:email, :encryptedPassword, 1)";

		namedParameters.addValue("email", email);
		namedParameters.addValue("encryptedPassword", passwordEncoder.encode(password));

		if (jdbc.update(query, namedParameters) > 0) {
			System.out.println("User " + email + " added successfully");
		}
		
	}

    // Add a new review
    public void addReview(String bookTitle, String reviewText, Long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "INSERT INTO reviews (book_title, review_text, user_id) " +
                       "VALUES (:bookTitle, :reviewText, :userId)";
        namedParameters.addValue("bookTitle", bookTitle);
        namedParameters.addValue("reviewText", reviewText);
        namedParameters.addValue("userId", userId);

        if (jdbc.update(query, namedParameters) > 0) {
            System.out.println("Review added successfully for book: " + bookTitle);
        }
    }

    // Get roles of a user by userId
    public List<String> getRolesById(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT sec_role.roleName " + "FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId " + "AND userId = :userId";
		namedParameters.addValue("userId", userId);
		return jdbc.queryForList(query, namedParameters, String.class);
	}

    // Get the roleId by roleName (used for assigning roles to users)
//    public Long getRoleIdByRoleName(String roleName) {
//        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//        String query = "SELECT roleId FROM sec_role WHERE roleName = :roleName";
//        namedParameters.addValue("roleName", roleName);
//        return jdbc.queryForObject(query, namedParameters, Long.class);
//    }

    // Add a role to a user (in the user_role table)
    public void addRole(Long userId, Long roleId) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO user_role (userId, roleId) " + "VALUES (:userId, :roleId)";

		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);
		jdbc.update(query, namedParameters);
    }
}


