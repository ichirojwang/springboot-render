package com.cmpt276.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.cmpt276.demo.models.User;
import com.cmpt276.demo.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/users/view")
    public String getAllUsers(Model model) {
        System.out.println("Getting all users");
        // get all users from database
        List<User> users = userRepo.findByOrderByUidAsc();
        // users.add(new User("bobby", "1234", 25));
        // users.add(new User("steve", "4567", 35));
        // users.add(new User("jacob", "34656", 45));
        // end of database call
        model.addAttribute("us", users);
        return "users/showAll";
    }

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newuser, HttpServletResponse response) {
        System.out.println("ADD user");
        String newName = newuser.get("name");
        String newPwd = newuser.get("password");
        int newSize = Integer.parseInt(newuser.get("size"));
        userRepo.save(new User(newName, newPwd, newSize));
        response.setStatus(201);
        return "redirect:/users/view";
    }
    
    @PostMapping("/users/remove")
    public String removeUser(@RequestParam("uid") String uid, HttpServletResponse response) {
        System.out.println("REMOVE user " + uid);
        // if (!userRepo.existsById(id)) {
        //     throw new UserNotFoundException("User with id " + id + " not found");
        // }
        int id = Integer.parseInt(uid);
        userRepo.deleteById(id);
        response.setStatus(202);
        return "redirect:/users/view";
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "users/login";
        }
        else {
            model.addAttribute("user", user);
            return "users/protected";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session) {
        //processing login
        String name = formData.get("name");
        String pwd = formData.get("password");
        List<User> userList = userRepo.findByNameAndPassword(name, pwd);
        if (userList.isEmpty()) {
            return "users/login";
        }
        else {
            User user = userList.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "users/protected";
        }
    }
    
    @GetMapping("/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }
    
}
