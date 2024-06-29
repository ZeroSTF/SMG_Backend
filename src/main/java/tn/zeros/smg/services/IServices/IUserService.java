package tn.zeros.smg.services.IServices;

import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.User;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    //AUTHENTICATION
    User registerUser(User user);
    LoginResponseDTO login(String email, String password);
    LoginResponseDTO login(String token);
    void logout();
    Boolean verifyToken(String token);

    //CRUD
    List<User> retrieveAllUsers();
    User retrieveUser(Long id);
    User retrieveUserByCode(String code);
    User addUser(User c);
    void removeUser(Long id) throws IOException;
    User modifyUser(User User);
    User loadUserByEmail(String email);
}
