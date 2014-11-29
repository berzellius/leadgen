package com.leadgen.web;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.UserRole;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.ClientRepository;
import com.leadgen.repository.UserRepository;
import com.leadgen.repository.UserRoleRepository;
import com.leadgen.util.UserLoginUtil;
import com.leadgen.dmodel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Set;

/**
 * Created by berz on 08.11.14.
 */
@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    UserRoleRepository userRoleRepository;

    private String redirectByRole(Set<UserRole> authorities){

        if(authorities.contains(userRoleRepository.findByRole(UserRole.Role.ADMIN).get(0))){
            return "redirect:/clients/";
        }

        if(authorities.contains(userRoleRepository.findByRole(UserRole.Role.CLIENT).get(0))){
            return "redirect:/managers";
        }

        if(authorities.contains(userRoleRepository.findByRole(UserRole.Role.MANAGER).get(0))){
            return "redirect:/";
        }

        return "redirect:/";
    }

    private boolean accessCheck(User user, User currentUser){
        if(user == null || currentUser == null) return false;

        if(userLoginUtil.userHaveRole(user, UserRole.Role.ADMIN)) return false;

        if(userLoginUtil.userHaveRole(user, UserRole.Role.CLIENT)){
            if(! userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN)) return false;
        }

        if(userLoginUtil.userHaveRole(user, UserRole.Role.MANAGER)){
            if(!(
                    userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN) ||
                            (
                                    userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT) &&
                                            currentUser.getClient().equals(user.getClient())
                            )
            )) return false;
        }

        return true;
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newUser(
            Model model,
            @RequestParam(value = "client")
            Long clientId,
            @RequestParam(value = "role")
            UserRole.Role role,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ){
        Client client = clientRepository.findOne(clientId);

        if(client == null) return "redirect:/";

        model.addAttribute("client", client);
        model.addAttribute("role", role);
        model.addAttribute("reason", reason);

        return "users/new";
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String editUser(
            Model model,
            @PathVariable(value = "id")
            Long userId,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ){
        User user = userRepository.findOne(userId);
        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(! accessCheck(user, currentUser))
            return redirectByRole(currentUser.getAuthorities());


        model.addAttribute("user", user);
        model.addAttribute("reason", reason);

        return "users/edit";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addUser(
            Model model,
            User user,
            @RequestParam(value = "role")
            UserRole.Role role,
            final RedirectAttributes redirectAttributes
    ){

        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(
                ( role == UserRole.Role.CLIENT && !userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN) ) ||
                        (role == UserRole.Role.MANAGER && (
                                    !userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT) ||
                                            !(currentUser.getClient().equals(user.getClient()))
                                )
                        )
                )
            return "redirect:/clients/" + user.getClient().getId().toString();

        try {
            userLoginUtil.addUserForClientWithRole(user, role);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("reason", e.getReason());
            redirectAttributes.addFlashAttribute("user", user);

            if(e.getReason().equals(WrongInputDataException.Reason.USER_ALREADY_EXISTS))
                return "redirect:/clients/" + user.getClient().getId().toString();

            return "redirect:/users/new?client="
                    .concat(user.getClient().getId().toString())
                    .concat("&role=")
                    .concat(role.toString());
        }

        if(role.equals(UserRole.Role.MANAGER)){
            return "redirect:/managers";
        }
        else{
            return "redirect:/clients/" + user.getClient().getId().toString();
        }
    }

    @RequestMapping(value = "/{id}/chpass", method = RequestMethod.POST)
    public String changePassword(
            Model model,
            @PathVariable(value = "id")
            Long userId,
            @RequestParam(value = "password")
            String password,
            final RedirectAttributes redirectAttributes
    ){
        User user = userRepository.findOne(userId);
        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(! accessCheck(user, currentUser))
            return redirectByRole(currentUser.getAuthorities());

        try {
            userLoginUtil.changePassForUser(user, password);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("reason", e.getReason());
        }

        return "redirect:/users/edit/".concat(user.getId().toString());
    }
}
