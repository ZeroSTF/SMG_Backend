package tn.zeros.smg.services.IServices;

import org.springframework.web.multipart.MultipartFile;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.User;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    //AUTHENTICATION
    User registerUser(User user);
    LoginResponseDTO login(String code, String password);
    LoginResponseDTO login(String token);
    void logout();
    Boolean verifyToken(String token);
    User getCurrentUser();

    //CRUD
    List<User> retrieveAllUsers();
    User retrieveUser(Long id);
    User addUser(User c);
    void removeUser(Long id) throws IOException;
    User modifyUser(User User);
    User loadUserByCode(String code);
    void confirmNewEmail(User user);
    List<User> chercherUser(String nom);
    String savePhoto(MultipartFile file) throws IOException;
    void deletePhoto(String fileName) throws IOException;
    Panier getUserPanier(Long userId);
    String soldeSum();
}
