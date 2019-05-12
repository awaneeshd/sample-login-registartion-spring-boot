package com.jishnu.sample.controller;

import javax.validation.Valid;
import com.jishnu.sample.model.User;
import com.jishnu.sample.model.UserInfo;
import com.jishnu.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value= {"/", "/login"}, method=RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        return model;
    }

    @RequestMapping(value= {"/signup"}, method=RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("user/signup");

        return model;
    }

    @RequestMapping(value= {"/signup"}, method=RequestMethod.POST)
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());

        if(userExists != null) {
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if(bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            userService.saveUser(user);
            model.addObject("msg", "User has been registered successfully!");
            model.addObject("user", new User());
            model.setViewName("user/login");
        }

        return model;
    }

    @RequestMapping(value= {"/home/dashboard"}, method=RequestMethod.GET)
    public ModelAndView home(Authentication authentication) {
        ModelAndView model = new ModelAndView();

        if(authentication == null) {
            model.setViewName("/login");
        }
        Set<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.addObject("role", String.join(",",new ArrayList<String>(roles)));
        model.addObject("email", user.getEmail());
        model.addObject("firstname", user.getFirstname());
        model.addObject("lastname", user.getLastname());
        if(roles.contains("ADMIN")) {
            model.setViewName("admin/home");
            List<User> users = userService.getAllUser();
            List<UserInfo> userList= new ArrayList<>();
            for(User user1: users) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(user1.getId());
                userInfo.setUsername(user1.getUsername());
                userInfo.setEmail(user1.getEmail());
                userInfo.setFirstname(user1.getFirstname());
                userInfo.setLastname(user1.getLastname());
                List<String> userRoles = user1.getRoles().stream().map(role1-> role1.getName()).collect(Collectors.toList());
                userInfo.setRoles(String.join(",",userRoles));
                userList.add(userInfo);
            }

            model.addObject("userList", userList);
            return model;
        }
        model.setViewName("home/home");
        return model;
    }

    @RequestMapping(value= {"/access_denied"}, method=RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }
}
