package tn.zeros.smg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.enums.TypeRole;
import tn.zeros.smg.repositories.RoleRepository;

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
	CommandLineRunner run(RoleRepository roleRepository){
		return args -> {
			if(roleRepository.findByType(TypeRole.ADMIN).isPresent() && roleRepository.findByType(TypeRole.USER).isPresent() && roleRepository.findByType(TypeRole.AGENT).isPresent()) return;
			roleRepository.save(new Role(null, "ADMIN", TypeRole.ADMIN));
			roleRepository.save(new Role(null, "USER", TypeRole.USER));
			roleRepository.save(new Role(null, "USER", TypeRole.AGENT));
		};


	}

}
