package tn.zeros.smg.services.IServices;

import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.User;

public interface IAuthService {
    User registerUser(User user);
    LoginResponseDTO login(String code, String password);
    void logout();
}
