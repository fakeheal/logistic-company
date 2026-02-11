package nbu.team11.seeders;

import nbu.team11.entities.Address;
import nbu.team11.entities.Office;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.OfficeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Order(1)
public class OfficeSeeder implements CommandLineRunner {
    private final OfficeRepository officeRepository;
    private final AddressRepository addressRepository;

    public OfficeSeeder(OfficeRepository officeRepository, AddressRepository addressRepository){
        this.officeRepository = officeRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (officeRepository.count() > 0) {
            return;
        }

        createOffice("Sofia Central", "123 Main St, Sofia, Bulgaria", "42.6977,23.3219");
        createOffice("Plovdiv Office", "45 Maritsa Blvd, Plovdiv, Bulgaria", "42.1354,24.7453");
        createOffice("Varna Office", "78 Sea Garden, Varna, Bulgaria", "43.2141,27.9147");
    }

    private void createOffice(String title, String formattedAddress, String coordinates) {
        Address address = new Address();
        address.setFormattedAddress(formattedAddress);
        address.setCoordinates(coordinates);
        addressRepository.save(address);

        Office office = new Office();
        office.setTitle(title);
        office.setAddress(address);
        officeRepository.save(office);
    }
}
