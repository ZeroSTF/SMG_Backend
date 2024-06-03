package tn.zeros.smg.services.IServices;

import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.User;
public interface IUserService {
    User registerUser(User user);
    LoginResponseDTO login(String email, String password);
    void logout();
    Boolean verifyToken(String token);
}
