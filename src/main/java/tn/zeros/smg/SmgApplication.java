package tn.zeros.smg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.enums.TypeRole;
import tn.zeros.smg.repositories.RoleRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.UserService;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
public class SmgApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmgApplication.class, args);
	}
	/////////////////////////////////////// Roles to be added by default on startup ///////////////////////////////////////////////////////
	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder, UserService userService){
		return args -> {
			//set every user's password in the database to "password" for testing purposes
			/*userRepository.findAll().forEach(user -> {
				user.setPassword(encoder.encode("password"));
				userRepository.save(user);
			});*/

			//set every user's role to old for testing purposes
			/*userRepository.findAll().forEach(user -> {
				Role oldRole = roleRepository.findById(3L).get();
				Set<Role> authorities = new HashSet<>();
				authorities.add(oldRole);
				user.setRole(authorities);
				userRepository.save(user);
			});*/

			// ensure all users have panier
			//userService.ensureAllUsersHavePaniers();

			if(!roleRepository.findByType(TypeRole.ADMIN).isPresent())
				roleRepository.save(new Role(1L, "ADMIN", TypeRole.ADMIN));
			if(!roleRepository.findByType(TypeRole.USER).isPresent())
				roleRepository.save(new Role(2L, "USER", TypeRole.USER));
			if(!roleRepository.findByType(TypeRole.OLD).isPresent())
				roleRepository.save(new Role(3L, "USER", TypeRole.OLD));
		};


	}

}
