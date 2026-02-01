package nbu.team11.seeders;

import nbu.team11.entities.Address;
import nbu.team11.entities.Client;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(3)
public class ClientSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientSeeder(ClientRepository clientRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (clientRepository.count() > 0) return;

        User user = new User(Role.CLIENT,
                "client1@example.com",
                passwordEncoder.encode("Client123!"),
                "Client",
                "Client");
        userRepository.save(user);

        Client client = new Client();
        client.setUser(user);
        client.setPhoneNumber("0888123456");

        client = clientRepository.save(client);

        Address address1 = new Address();
        address1.setFormattedAddress("456 Client Street, Sofia, Bulgaria");
        address1.setCoordinates("42.6977,23.3219");
        client.addAddress(address1);

        Address address2 = new Address();
        address2.setFormattedAddress("789 Second Ave, Sofia, Bulgaria");
        address2.setCoordinates("42.7000,23.3333");
        client.addAddress(address2);

        clientRepository.save(client);
    }
}
