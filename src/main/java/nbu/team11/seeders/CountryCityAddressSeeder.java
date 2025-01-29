package nbu.team11.seeders;

import lombok.RequiredArgsConstructor;
import nbu.team11.entities.Address;
import nbu.team11.entities.City;
import nbu.team11.entities.Country;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.CityRepository;
import nbu.team11.repositories.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CountryCityAddressSeeder implements ApplicationRunner {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final AddressRepository addressRepository;

    private static final Logger logger = LoggerFactory.getLogger(CountryCityAddressSeeder.class);

    @Override
    public void run(ApplicationArguments args) {
        if (args.getOptionValues("seeder") != null) {
            List<String> seeder = Arrays.asList(args.getOptionValues("seeder").get(0).split(","));
            if (seeder.contains("all")) {
                seedDatabase();
                logger.info("Seeders executed successfully.");
            }
        } else {
            logger.info("Seeders skipped.");
        }
    }

    private void seedDatabase() {
        String countryName = "Bulgaria";
        if (!countryRepository.existsByName(countryName)) {
            Country country = countryRepository.save(Country.builder().name(countryName).build());
            logger.info("Country {} seeded.", countryName);

            Map<String, List<Map<String, String>>> cityWithAddresses = Map.of(
                    "Sofia", List.of(
                            Map.of("street", "Vitosha 182A", "postalCode", "1404"),
                            Map.of("street", "Tsarigradsko shose 45", "postalCode", "5557")
                    ),
                    "Plovdiv", List.of(
                            Map.of("street", "Vasil Aprilov 4", "postalCode", "1423"),
                            Map.of("street", "Ivan Vazov 3", "postalCode", "4222")
                    ),
                    "Varna", List.of(
                            Map.of("street", "Vladislav Varnenchik 11B", "postalCode", "7625"),
                            Map.of("street", "Devnya 8", "postalCode", "1001")
                    )
            );

            cityWithAddresses.forEach((cityName, addresses) -> {
                if (!cityRepository.existsByName(cityName)) {
                    City city = cityRepository.save(City.builder().name(cityName).country(country).build());
                    logger.info("City {} seeded.", cityName);

                    addresses.forEach(address -> {
                        addressRepository.save(
                                Address.builder()
                                        .city(city)
                                        .street(address.get("street"))
                                        .postalCode(address.get("postalCode"))
                                        .build()
                        );
                        logger.info("Address {} seeded.", address);
                    });
                }
            });
        }
    }
}
