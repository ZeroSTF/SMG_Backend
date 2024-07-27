package tn.zeros.smg.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.Commande;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.enums.CommandeStatus;
import tn.zeros.smg.repositories.CommandeRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.IServices.ICommandeService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommandeService implements ICommandeService {
    private final CommandeRepository commandeRepository;
    private final UserRepository userRepository;

    @Override
    public Commande getCommandeById(Long commandeId) {
        return commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande not found with id: " + commandeId));
    }

    @Override
    public List<Commande> getUserCommandes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return commandeRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Commande updateCommandeStatus(Long commandeId, CommandeStatus status) {
        Commande commande = getCommandeById(commandeId);
        commande.setStatus(status);
        return commandeRepository.save(commande);
    }
}
