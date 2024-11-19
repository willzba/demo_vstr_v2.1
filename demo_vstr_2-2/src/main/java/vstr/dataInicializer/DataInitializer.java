package vstr.dataInicializer;

import org.springframework.boot.CommandLineRunner;
import vstr.model.Role;
import vstr.repository.UserRoleRepository;

public class DataInitializer implements CommandLineRunner {

    private UserRoleRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar y crear roles si no existen
        if (rolRepository.findByName("ROLE_USER") == null) {
            Role name = new Role();
            rolRepository.findByName("ROLE_USER");
            rolRepository.save(name);
        }
    }
}
