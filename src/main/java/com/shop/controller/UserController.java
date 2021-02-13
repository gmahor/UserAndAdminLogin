package com.shop.controller;

import java.io.FileInputStream;
import java.security.Principal;

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
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String name = p.getName();
		User loginUser = this.userRepo.getUserByUserName(name);
		m.addAttribute("loginuser", loginUser);

	}

	@GetMapping("/edit-user/{id}")
	public String editUserForm(@PathVariable("id") int id, Model m) {
		User user = this.userRepo.findById(id).get();
		m.addAttribute("user", user);
		return "user/edit_user";
	}

	@PostMapping("/updateProcess")
	public String editUserProcess(@ModelAttribute User user, HttpSession session) {

		try {

			User editUser = this.userRepo.save(user);

			System.out.println(editUser.getUsername() + " User updated successfully");

			session.setAttribute("message",
					new Message(editUser.getUsername() + " User Updated Successfully... ", "alert-warning"));

			return "redirect:/logout";

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something Wrong While Updating User...", "alert-danger"));

			e.printStackTrace();
			return "user/edit_user";

		}

	}

	@GetMapping("/changeUser-password")
	public String changeUserPasswordForm() {
		return "user/changingUserPassword";
	}

	@PostMapping("/changeProccess")
	public String changePasswordProcess(@RequestParam("oldPassword") String oldpassword,
			@RequestParam("newPassword") String newpassword, Principal p, HttpSession session) {

		try {
			String name = p.getName();
			User user = this.userRepo.getUserByUserName(name);

			String password = user.getPassword();

			if (bCryptPasswordEncoder.matches(oldpassword, password)) {
				String NewPassword = bCryptPasswordEncoder.encode(newpassword);

				user.setPassword(NewPassword);

				this.userRepo.save(user);

				System.out.println("Password Change successfully");

				return "redirect:/logout";
			} else {
				System.out.println("your password is not match...");
				return "user/changingUserPassword";
			}
		} catch (Exception e) {
			session.setAttribute("message",
					new Message("Something Went Wrong While Change Password...", "alert-danger"));
			return "user/changingUserPassword";
		}

	}

}
