package ca.sheridancollege.bhavsvir.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.bhavsvir.database.DatabaseAccess;

@Controller
public class HomeController {

    @Autowired
    @Lazy
    private DatabaseAccess da;

    // Home page
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Secure page
    @GetMapping("/secure")
    public String secureIndex() {
        return "/secure/index";
    }

    // Login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Permission Denied page
    @GetMapping("/permission-denied")
    public String permissionDenied() {
        return "/error/permission-denied";
    }

    // Registration form
    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    // Registration submission
    @PostMapping("/register")
	public String postRegister(@RequestParam String username, @RequestParam String password) {
		
		da.addUser(username, password);
		Long userId = da.findUserAccount(username).getUserId();
		// this next line is dangerous! For extra credit, try making a DatabaseAccess method to find a roleId associate with a role of “ROLE_USER” and add the “correct” id every time ☺
		da.addRole(userId, Long.valueOf(2));

		
		return "redirect:/";
	}


    // Page to check the functionality of secure routes
    @GetMapping("/secure/check")
    public String check() {
        return "/secure/check";
    }
}