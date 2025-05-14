package ca.sheridancollege.bhavsvir.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.sheridancollege.bhavsvir.database.DatabaseAccess;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    @Lazy
    private DatabaseAccess da;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by username (or email)
    	ca.sheridancollege.bhavsvir.beans.User user = da.findUserAccount(username);

        // If the user doesn't exist, throw an exception
        if (user == null) {
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

        // Get the list of roles for the user
        List<String> roleNameList = da.getRolesById(user.getUserId());

        // Convert roles into GrantedAuthority
        List<GrantedAuthority> grantList = new ArrayList<>();
        if (roleNameList != null) {
            for (String role : roleNameList) {
                grantList.add(new SimpleGrantedAuthority(role));
            }
        }

        UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getEncryptedPassword(), grantList);
		return userDetails;
    }
}
