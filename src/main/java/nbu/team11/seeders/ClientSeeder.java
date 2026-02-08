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

        User user1 = new User(
                Role.CLIENT,
                "client1@example.com",
                passwordEncoder.encode("Client123!"),
                "Client",
                "One"
        );
        userRepository.save(user1);

        Client client1 = new Client();
        client1.setUser(user1);
        client1.setPhoneNumber("0888123456");

        client1 = clientRepository.save(client1);

        Address c1Address1 = new Address();
        c1Address1.setFormattedAddress("456 Client Street, Sofia, Bulgaria");
        c1Address1.setCoordinates("42.6977,23.3219");
        client1.addAddress(c1Address1);

        Address c1Address2 = new Address();
        c1Address2.setFormattedAddress("789 Second Ave, Sofia, Bulgaria");
        c1Address2.setCoordinates("42.7000,23.3333");
        client1.addAddress(c1Address2);

        clientRepository.save(client1);

        User user2 = new User(
                Role.CLIENT,
                "client2@example.com",
                passwordEncoder.encode("Client123!"),
                "Client",
                "Two"
        );
        userRepository.save(user2);

        Client client2 = new Client();
        client2.setUser(user2);
        client2.setPhoneNumber("0888999888");

        client2 = clientRepository.save(client2);

        Address c2Address1 = new Address();
        c2Address1.setFormattedAddress("12 Vitosha Blvd, Sofia, Bulgaria");
        c2Address1.setCoordinates("42.6885,23.3196");
        client2.addAddress(c2Address1);

        Address c2Address2 = new Address();
        c2Address2.setFormattedAddress("34 Graf Ignatiev St, Sofia, Bulgaria");
        c2Address2.setCoordinates("42.6942,23.3251");
        client2.addAddress(c2Address2);

        clientRepository.save(client2);
    }
}
