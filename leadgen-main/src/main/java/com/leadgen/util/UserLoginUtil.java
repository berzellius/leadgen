package com.leadgen.util;

import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.dmodel.User;
import com.leadgen.dmodel.UserRole;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 06.11.14.
 */
@Service
public interface UserLoginUtil {

    public User getCurrentLogInUser();

    boolean userHaveRole(User currentUser, UserRole.Role admin);

    void addUserForClientWithRole(User user, UserRole.Role role) throws WrongInputDataException;

    void changePassForUser(User user, String password) throws WrongInputDataException;
}
