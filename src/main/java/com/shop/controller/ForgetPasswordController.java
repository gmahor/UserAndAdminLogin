package com.shop.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shop.dao.UserRepo;
import com.shop.entities.User;
import com.shop.helper.Message;
import com.shop.services.EmailService;

@Controller
public class ForgetPasswordController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EmailService emailService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/forget-password")
	public String forgetPasswordForm() {
		return "forget_password";
	}

	@PostMapping("/forgetPasswordProcess")
	public String forgetPassProcess(@RequestParam("to") String to, HttpSession session) {

		try {

			Random random = new Random();
			int otp = random.nextInt(99999);

			System.out.println("OTP : " + otp);

			String subject = "Forget Password";
			String message = "<div style='margin: auto; width: 80%; border: 3px solid black;padding: 10px;background-color:black;'>"
					+ "<h1 style='text-align: center;color:white;' >" + "<u> Password Reset  </u>" + "</h1>" + "</hr>"
					+ "<h3 style='text-align: center; border: 3px solid blue;color:white;background-color:blue;'> Your Reset Opt : "
					+ otp + "</h3>"
					+ "<h5 style='text-align: center;color:white;'>Ignored if you don't want to reset you password....</h5>"
					+ "</div> ";

			boolean flag = this.emailService.sendEmail(to, subject, message);

			if (flag) {

				session.setAttribute("myOtp", otp);

				session.setAttribute("email", to);

				session.setAttribute("message", new Message(
						"We While Send A Reset-Password Link In Your Email Pls Check Your Email...", "alert-success"));

				return "redirect:verify-otp";

			} else {
				session.setAttribute("message", new Message("Email Was Not Send Try Again...  ", "alert-danger"));
				return "forget_password";
			}

		} catch (Exception e) {
			session.setAttribute("message", new Message("You Enter The Wrong Email...", "alert-danger"));
			System.out.println("Something went wrong in handler while send email...");
			return "forget_password";
		}
	}

	@GetMapping("/verify-otp")
	public String verifyOtpFrom(Model m, HttpSession session) {
		m.addAttribute("title", "(Shop)Verifying otp");
		return "verify_otp";
	}

	@PostMapping("/verifyOtpProcess")
	public String verifyOtpProcess(@RequestParam("otp") int otp, HttpSession session) {

		try {

			Integer myOtp = (Integer) session.getAttribute("myOtp");

			String email = (String) session.getAttribute("email");

			if (myOtp == otp) {

				User user = this.userRepo.getUserByEmail(email);

				if (user == null) {
					session.setAttribute("message",
							new Message("User does not exits with this emails..", "alert-danger"));

					return "redirect:/forget-password";
				} else {
					return "redirect:/reset-password";
				}

			} else {
				session.setAttribute("message", new Message("You Enter A Wrong OPT..", "alert-danger"));
				return "verify_otp";
			}

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something Went Wrong While Verifing OPT", "alert-danger"));
			return "verify_otp";

		}

	}

	@GetMapping("/reset-password")
	public String resetPasswordForm(HttpSession session) {

		return "reset_password";
	}

	@PostMapping("/resetProccess")
	public String resetPasswordProcess(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, HttpSession session) {

		try {

			if (newPassword.equals(confirmPassword)) {

				String email = (String) session.getAttribute("email");

				User user = this.userRepo.getUserByEmail(email);

				String encodePassword = this.bCryptPasswordEncoder.encode(newPassword);

				user.setPassword(encodePassword);

				this.userRepo.save(user);

				session.setAttribute("message",
						new Message("Your Password Is Reset Successfully!" + "Login Again... ", "alert-success"));

				return "reset_password";
			} else {

				session.setAttribute("message", new Message("Your Password Is Not Match... ", "alert-danger"));
				return "reset_password";
			}

		} catch (Exception e) {
			session.setAttribute("message",
					new Message("Something Went Wrong While Reseting Password...", "alert-danger"));
			return "reset_password";

		}

	}

}
