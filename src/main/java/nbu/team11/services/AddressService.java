package nbu.team11.services;

import nbu.team11.entities.Address;
import nbu.team11.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
//TODO: Create interface
@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    public List<Address> getAddressByLocation(String postalCode, String street, String country) {
        return addressRepository.findByPostalCodeAndStreetAndCity_Country_Name(postalCode, street, country);
    }

    public Optional<Address> tryGetAddressBy(String street) {
        return addressRepository.findByStreet(street);
    }

    //TODO: Rewrite method in repo
    public List<Address> getAddressesByCountry(String country) {
        //return addressRepository.findByCountry(country);
        return null;
    }

    //TODO: Rewrite method in repo
    public List<Address> getAddressesByCity(String city) {
        //return addressRepository.findByCity(city);
        return null;
    }

    public Address updateAddress(Integer id, Address updatedAddress) {
        Address address = getAddressById(id);
        if (address == null) return null;
        address.setStreet(updatedAddress.getStreet());
        address.setPostalCode(updatedAddress.getPostalCode());
        address.setCity(updatedAddress.getCity());
        return addressRepository.save(address);
    }

    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }
}

