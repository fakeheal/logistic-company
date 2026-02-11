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

        // Client 1 - Sofia
        Client c1 = createClient("client1@example.com", "Client123!", "Petar", "Stoyanov", "0888123456");
        addAddress(c1, "456 Client Street, Sofia, Bulgaria", "42.6977,23.3219");
        addAddress(c1, "789 Second Ave, Sofia, Bulgaria", "42.7000,23.3333");
        clientRepository.save(c1);

        // Client 2 - Sofia
        Client c2 = createClient("client2@example.com", "Client123!", "Anna", "Koleva", "0888999888");
        addAddress(c2, "12 Vitosha Blvd, Sofia, Bulgaria", "42.6885,23.3196");
        addAddress(c2, "34 Graf Ignatiev St, Sofia, Bulgaria", "42.6942,23.3251");
        clientRepository.save(c2);

        // Client 3 - Plovdiv
        Client c3 = createClient("client3@example.com", "Client123!", "Dimitar", "Hristov", "0877111222");
        addAddress(c3, "5 Kapana District, Plovdiv, Bulgaria", "42.1490,24.7510");
        clientRepository.save(c3);

        // Client 4 - Varna
        Client c4 = createClient("client4@example.com", "Client123!", "Silvia", "Marinova", "0899333444");
        addAddress(c4, "22 Slivnitsa Blvd, Varna, Bulgaria", "43.2073,27.9105");
        addAddress(c4, "8 Primorski Park, Varna, Bulgaria", "43.2100,27.9250");
        clientRepository.save(c4);
    }

    private Client createClient(String email, String password, String firstName, String lastName, String phone) {
        User user = new User(Role.CLIENT, email, passwordEncoder.encode(password), firstName, lastName);
        userRepository.save(user);

        Client client = new Client();
        client.setUser(user);
        client.setPhoneNumber(phone);
        return clientRepository.save(client);
    }

    private void addAddress(Client client, String formattedAddress, String coordinates) {
        Address address = new Address();
        address.setFormattedAddress(formattedAddress);
        address.setCoordinates(coordinates);
        client.addAddress(address);
    }
}
