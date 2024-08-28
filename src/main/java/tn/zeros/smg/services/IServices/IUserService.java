package tn.zeros.smg.services.IServices;

import org.springframework.web.multipart.MultipartFile;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.controllers.DTO.UserDTO;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.User;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    Boolean verifyToken(String token);

    User getCurrentUser();

    String getRoleString(User user);

    List<UserDTO> retrieveAllUsers();

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

    void ensureUserHasPanier(User user);
}
