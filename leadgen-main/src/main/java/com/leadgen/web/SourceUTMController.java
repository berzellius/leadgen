package com.leadgen.web;

import com.leadgen.dmodel.SourceUTM;
import com.leadgen.dmodel.User;
import com.leadgen.dmodel.UserRole;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.SourceUTMRepository;
import com.leadgen.specifications.SourceUTMSpecifications;
import com.leadgen.service.UTMService;
import com.leadgen.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by berz on 27.11.14.
 */
@Controller
@RequestMapping(value = "/utm")
public class SourceUTMController {

    @Autowired
    SourceUTMRepository sourceUTMRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    UTMService utmService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(
            Model model,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ) throws IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        if(! userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            throw new IllegalAccessException("this is for admin only");

        if(reason != null) model.addAttribute("reason", reason);

        List<SourceUTM> sourceUTMList = (List<SourceUTM>) sourceUTMRepository.findAll(SourceUTMSpecifications.notDeleted());

        model.addAttribute("utmlist", sourceUTMList);

        return "utm/all";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(
            Model model,
            SourceUTM sourceUTM,
            final RedirectAttributes redirectAttributes
    ) throws IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        if(! userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            throw new IllegalAccessException("this is for admin only");

        try {
            utmService.addSourceUTM(sourceUTM);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("reason", e.getReason());
        }

        return "redirect:/utm/";
    }

    @RequestMapping(value = "/{id}", params = "delete", method = RequestMethod.DELETE)
    public String delete(
            @PathVariable(value = "id")
            Long sourceUTMId
    ) throws IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        if(! userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            throw new IllegalAccessException("this is for admin only");

        utmService.deleteSourceUTM(sourceUTMId);

        return "redirect:/utm";
    }

}
