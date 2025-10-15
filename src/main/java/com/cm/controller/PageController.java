package com.cm.controller;

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
import com.cm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class PageController {

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
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
    public String processRegister(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult rBindingResult, HttpSession session) {
        System.out.println("Processing Register Form"   );
        
        //fetch form data
        //UserForm
        System.out.println(userForm);

        //validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        //save the data in database

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setEnabled(false);
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setProfilePic("https://media.istockphoto.com/id/1300845620/vector/user-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg?s=612x612&w=0&k=20&c=yBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o=");


        User saveUser = userService.saveUser(user);
        System.out.println("Saved User: " + saveUser);

        //message = "Successfully Registered";

        //add the message:

        Message message = Message.builder()
        .content("Registration Successful")
        .type(MessageType.blue).build();

        session.setAttribute("message", message);

        //redirect to login page

        return "redirect:/register";
    }
}
