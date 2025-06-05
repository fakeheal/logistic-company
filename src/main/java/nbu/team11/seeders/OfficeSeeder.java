package nbu.team11.seeders;

import nbu.team11.entities.Address;
import nbu.team11.entities.Office;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.OfficeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
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

        Office office1 = new Office();
        office1.setTitle("TheBestOffice");

        Address officeAddress = new Address();
        officeAddress.setCoordinates("42.6977,23.3219");
        officeAddress.setFormattedAddress("123 Main St, Sofia, Bulgaria");
        addressRepository.save(officeAddress);
        office1.setAddress(officeAddress);
        officeRepository.save(office1);
    }
}
