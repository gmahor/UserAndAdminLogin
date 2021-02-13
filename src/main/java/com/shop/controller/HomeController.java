package com.shop.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shop.dao.UserRepo;
import com.shop.entities.User;
import com.shop.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/")
	public String homepage(Model m, Principal p) {

		try {

			String name = p.getName();

			User user = userRepo.getUserByUserName(name);

			m.addAttribute("user", user);

		} catch (Exception e) {
			System.out.println("There is no user....");
		}

		m.addAttribute("title", "Shop");
		return "index";
	}

	@PostMapping("/signup")
	public String signupForm(Model m) {
		m.addAttribute("title", "Signup");
		return "signup";
	}

	@PostMapping("/signupProcess")
	public String signupProcess(@ModelAttribute User user, @RequestParam("confirmPass") String confirm,
			HttpSession session) {

		try {
			if (user.getPassword().equals(confirm)) {

				user.setRole("ROLE_USER");

				user.setEnable(true);

				String encode = this.bCryptPasswordEncoder.encode(user.getPassword());

				user.setPassword(encode);

				this.userRepo.save(user);

				session.setAttribute("message", new Message("User Added Successfully...", "alert-success"));

				System.out.println("User Added Successfully ");

				return "redirect:/login";

			} else {
				session.setAttribute("message", new Message("Password Con't Matched...", "alert-danger"));

				System.out.println("Something Went wrong");
				return "redirect:/signup";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "signup";
		}

	}

	@GetMapping("/login")
	public String login(HttpSession session) {

		try {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (!(authentication instanceof AnonymousAuthenticationToken)) {

				User user = userRepo.getUserByUserName(authentication.getName());

				if (user.getRole().contains("USER")) {
					session.setAttribute("message", new Message("You are aleardy login", "alert-warning"));
					return "redirect:/user/userDashboard";
				} else if (user.getRole().contains("ADMIN")) {
					session.setAttribute("message", new Message("You are aleardy login", "alert-warning"));
					return "redirect:/admin/adminDashboard";
				} else {
					return "redirect:/";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "login";
	}

	@GetMapping("/admin/adminDashboard")
	public String adminDashboard(Model m, Principal p) {

		String name = p.getName();

		User loginuser = userRepo.getUserByUserName(name);

		m.addAttribute("loginuser", loginuser);

		m.addAttribute("title", "Admin");
		return "admin/admindashboard";
	}

	@GetMapping("/user/userDashboard")
	public String userDashboard(Model m, Principal p) {
		String name = p.getName();
		User loginUser = userRepo.getUserByUserName(name);
		m.addAttribute("loginuser", loginUser);
		m.addAttribute("title", "User");
		return "user/userDashboard";
	}

	@RequestMapping("/ex")
	public String ex() {
		return "ex";
	}

}
