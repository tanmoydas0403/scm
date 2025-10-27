package com.cm.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cm.entities.User;
import com.cm.forms.UserForm;
import com.cm.helpers.Message;
import com.cm.helpers.MessageType;
import com.cm.repository.UserRepository;
import com.cm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;




@Controller
public class PageController {

    private final UserService userService;

    private final UserRepository userRepo;

    public PageController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("home");
        model.addAttribute("name", "cm");
        return "home";
    }

    //about route

    @RequestMapping("/about")
    public String aboutPage() {
        System.out.println("About Page loading");
        return "about";
    }
    //services route

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("About Sevices loading");
        return "services";
    }

    //contact route
    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    //login route
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    //register route
    @GetMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        // userForm.setName("John Doe");
        // userForm.setEmail("jhon@gmail.com");
        // userForm.setAbout("I am a Java Developer");
        // userForm.setPhoneNumber("1234567890");
        // userForm.setPassword("password");
        model.addAttribute("userForm", userForm); // ✅ corrected name
        return "register";
    }

    //Processing the register form

    @PostMapping(value = "/do-register")
    public String processRegister(
        @Valid @ModelAttribute("userForm") UserForm userForm,
        BindingResult rBindingResult,
        HttpSession session) {

        System.out.println("Processing Register Form");
        System.out.println(userForm);

        // Validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Check if email already exists
            Optional<User> existingUser = userRepo.findByEmail(userForm.getEmail());

            if (existingUser.isPresent()) {
                // Email already exists - show error
                Message message = Message.builder()
                    .content("This email is already registered. Please use a different email or login.")
                    .type(MessageType.red)
                    .build();
                session.setAttribute("message", message);
                return "register";
            }

            // Create new user
            User user = new User();
            user.setName(userForm.getName());
            user.setEmail(userForm.getEmail());
            user.setPassword(userForm.getPassword());
            user.setAbout(userForm.getAbout());
            user.setEnabled(false);
            user.setPhoneNumber(userForm.getPhoneNumber());
            user.setProfilePic("https://media.istockphoto.com/id/1300845620/vector/user-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg?s=612x612&w=0&k=20&c=yBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o=");

            // Save user
            User savedUser = userService.saveUser(user);
            System.out.println("Saved User: " + savedUser);

            // Success message
            Message message = Message.builder()
                .content("Registration Successful! You can now login.")
                .type(MessageType.green)
                .build();
            session.setAttribute("message", message);

            return "redirect:/register";

        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations
            System.err.println("Database constraint violation: " + e.getMessage());

            Message message = Message.builder()
                .content("This email is already registered. Please use a different email.")
                .type(MessageType.red)
                .build();
            session.setAttribute("message", message);

            return "register";

        } catch (Exception e) {
            // Handle any other errors
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();

            Message message = Message.builder()
                .content("Registration failed. Please try again later.")
                .type(MessageType.red)
                .build();
            session.setAttribute("message", message);

            return "register";
        }
    }


}

//package com.cm.controller;
//
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.cm.entities.User;
//import com.cm.forms.UserForm;
//import com.cm.helpers.Message;
//import com.cm.helpers.MessageType;
//import com.cm.repository.UserRepository;
//import com.cm.service.UserService;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import java.util.Optional;
//
//@Controller
//public class PageController {
//
//    private final UserService userService;
//    private final UserRepository userRepo;
//
//    public PageController(UserService userService, UserRepository userRepo) {
//        this.userService = userService;
//        this.userRepo = userRepo;
//    }
//
//    @GetMapping("/")
//    public String index() {
//        return "redirect:/home";
//    }
//
//    @RequestMapping("/home")
//    public String home(Model model) {
//        System.out.println("home");
//        model.addAttribute("name", "cm");
//        return "home";
//    }
//
//    //about route
//    @RequestMapping("/about")
//    public String aboutPage() {
//        System.out.println("About Page loading");
//        return "about";
//    }
//
//    //services route
//    @RequestMapping("/services")
//    public String servicesPage() {
//        System.out.println("About Services loading");
//        return "services";
//    }
//
//    //contact route
//    @GetMapping("/contact")
//    public String contact() {
//        return "contact";
//    }
//
//    //login route
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
//
//    //register route
//    @GetMapping("/register")
//    public String register(Model model) {
//        UserForm userForm = new UserForm();
//        model.addAttribute("userForm", userForm);
//        return "register";
//    }
//
//    //Processing the register form
//    @PostMapping(value = "/do-register")
//    public String processRegister(
//            @Valid @ModelAttribute("userForm") UserForm userForm,
//            BindingResult rBindingResult,
//            HttpSession session) {
//
//        System.out.println("Processing Register Form");
//        System.out.println(userForm);
//
//        // Validate form data
//        if (rBindingResult.hasErrors()) {
//            return "register";
//        }
//
//        try {
//            // Check if email already exists
//            Optional<User> existingUser = userRepo.findByEmail(userForm.getEmail());
//
//            if (existingUser.isPresent()) {
//                // Email already exists - show error
//                Message message = Message.builder()
//                        .content("This email is already registered. Please use a different email or login.")
//                        .type(MessageType.red)
//                        .build();
//                session.setAttribute("message", message);
//                return "register";
//            }
//
//            // Create new user
//            User user = new User();
//            user.setName(userForm.getName());
//            user.setEmail(userForm.getEmail());
//            user.setPassword(userForm.getPassword());
//            user.setAbout(userForm.getAbout());
//            user.setEnabled(false);
//            user.setPhoneNumber(userForm.getPhoneNumber());
//            user.setProfilePic("https://media.istockphoto.com/id/1300845620/vector/user-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg?s=612x612&w=0&k=20&c=yBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o=");
//
//            // Save user
//            User savedUser = userService.saveUser(user);
//            System.out.println("Saved User: " + savedUser);
//
//            // Success message
//            Message message = Message.builder()
//                    .content("Registration Successful! You can now login.")
//                    .type(MessageType.green)
//                    .build();
//            session.setAttribute("message", message);
//
//            return "redirect:/register";
//
//        } catch (DataIntegrityViolationException e) {
//            // Handle database constraint violations
//            System.err.println("Database constraint violation: " + e.getMessage());
//
//            Message message = Message.builder()
//                    .content("This email is already registered. Please use a different email.")
//                    .type(MessageType.red)
//                    .build();
//            session.setAttribute("message", message);
//
//            return "register";
//
//        } catch (Exception e) {
//            // Handle any other errors
//            System.err.println("Error during registration: " + e.getMessage());
//            e.printStackTrace();
//
//            Message message = Message.builder()
//                    .content("Registration failed. Please try again later.")
//                    .type(MessageType.red)
//                    .build();
//            session.setAttribute("message", message);
//
//            return "register";
//        }
//    }
//}
