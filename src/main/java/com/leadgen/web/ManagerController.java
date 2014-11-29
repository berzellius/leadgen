package com.leadgen.web;

import com.leadgen.dmodel.UserRole;
import com.leadgen.util.UserLoginUtil;
import com.leadgen.dmodel.User;
import com.leadgen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by berz on 09.11.14.
 */
@Controller
@RequestMapping("/managers")
public class ManagerController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String managers(
            Model model
    ){
        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(currentUser != null && userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT)){

            List<User> managerUsers = userRepository.findByClientHasRole(currentUser.getClient(), UserRole.Role.MANAGER);
            model.addAttribute("managerUsers", managerUsers);
            model.addAttribute("client", currentUser.getClient());
        }
        else return "/";

        return "managers/all";
    }

}
