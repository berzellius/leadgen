package com.leadgen.util;

import com.leadgen.dmodel.UserRole;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.UserRoleRepository;
import com.leadgen.dmodel.User;
import com.leadgen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Created by berz on 06.11.14.
 */
@Service
@Transactional
public class UserLoginUtilImpl implements UserLoginUtil {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public User getCurrentLogInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        if (name == null) return null;

        return (User) userDetailsService.loadUserByUsername(name);
    }

    @Override
    public boolean userHaveRole(User user, UserRole.Role role) {
        return user.getAuthorities().contains(userRoleRepository.findByRole(role).get(0));
    }

    @Override
    public void addUserForClientWithRole(User user, final UserRole.Role role) throws WrongInputDataException {

        if(user.getUsername() == null || user.getUsername().equals(""))
            throw new WrongInputDataException("username is required for new user!", WrongInputDataException.Reason.USERNAME_FIELD);

        if(userRepository.findByUsername(user.getUsername()) != null)
            throw new WrongInputDataException("user with username = ".concat(user.getUsername()).concat(" already exists"), WrongInputDataException.Reason.DUPLICATE_USERNAME);

        if(user.getPassword() == null || user.getPassword().equals(""))
            throw new WrongInputDataException("password is required for new user!", WrongInputDataException.Reason.PASSWORD_FIELD);


        if(role.equals(UserRole.Role.CLIENT)){
            List<User> userExists = userRepository.findByClientHasRole(user.getClient(), role);
            if(userExists != null && userExists.size() > 0)
                throw new WrongInputDataException("client user already exists", WrongInputDataException.Reason.USER_ALREADY_EXISTS);
        }

        Set<UserRole> userRoles = new HashSet<UserRole>(){{add(userRoleRepository.findByRole(role).get(0));}};
        user.setAuthorities(userRoles);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(user.getPassword(), null));

        userRepository.save(user);

    }

    @Override
    public void changePassForUser(User user, String password) throws WrongInputDataException {
        if(password == null || password.equals("")) throw new WrongInputDataException("not empty password required!", WrongInputDataException.Reason.PASSWORD_FIELD);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(password, null));

        userRepository.save(user);
    }
}
