package com.shop.controller;


import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shop.dao.UserRepo;
import com.shop.entities.User;
import com.shop.helper.Message;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String name = p.getName();

		User loginuser = userRepo.getUserByUserName(name);

		m.addAttribute("loginuser", loginuser);

	}

	@GetMapping("/allAdmin")
	public String viewAllAdmin(Model m) {

		List<User> users = this.userRepo.findAll();

		m.addAttribute("users", users);

		return "admin/all_admin";
	}

	@PostMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable("id") int id, HttpSession session) {

		try {

			userRepo.deleteById(id);
			session.setAttribute("message", new Message("User Deleted Successfully...", "alert-warning"));
			return "redirect:/admin/allAdmin";

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something Went While Deleting User...", "alert-danger"));
			e.printStackTrace();
			return "redirect:/admin/allAdmin";
		}

	}

	@PostMapping("/update-user/{id}")
	public String updateUserForm(@PathVariable("id") int id, Model m) {

		User user = userRepo.findById(id).get();
		m.addAttribute("user", user);
		return "admin/update_user";

	}

	@PostMapping("/updateProcess")
	public String udpateProcess(@ModelAttribute User user, HttpSession session) {

		try {

			System.out.println("User : " + user);

			User edituser = this.userRepo.save(user);
			session.setAttribute("message",
					new Message(edituser.getUsername() + " : Details Updated Successfully...", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something wrong while update user...", "alert-danger"));
			e.printStackTrace();
		}
		return "redirect:/admin/allAdmin";

	}

	@GetMapping("/changePassword")
	public String changePasswordForm() {
		return "admin/change_password";
	}

	@PostMapping("/changeProccess")
	public String changePasswordProcess(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal p, HttpSession session) {

		try {

			String name = p.getName();
			User user = this.userRepo.getUserByUserName(name);

			String password = user.getPassword();

			if (this.bCryptPasswordEncoder.matches(oldPassword, password)) {

				String NewPassword = this.bCryptPasswordEncoder.encode(newPassword);

				user.setPassword(NewPassword);

				this.userRepo.save(user);

				return "redirect:/logout";

			} else {
				session.setAttribute("message", new Message("Password Can't Match Try Again...", "alert-danger"));

				return "admin/change_password";
			}
		} catch (Exception e) {
			session.setAttribute("message",
					new Message("Something Went Wrong While Changing Password...", "alert-danger"));
			return "admin/change_password";

		}

	}

}
