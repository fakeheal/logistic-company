package nbu.team11.services;

import nbu.team11.entities.Office;
import nbu.team11.repositories.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
//TODO: Create an interface
@Service
public class OfficeService {
    @Autowired
    private OfficeRepository officeRepository;

    //TODO: Return dto with automapper

    public Office createOffice(Office office) {
        return officeRepository.save(office);
    }

    //TODO: Return dto with automapper

    public Office getOfficeById(Integer id) {
        return officeRepository.findById(id).orElse(null);
    }

    //TODO: Return dto with automapper

    public Office getOfficeByName(String name) {
        return officeRepository.findByTitle(name);
    }

    //TODO: Return dto with automapper
    //TODO: Rewrite repo
    public List<Office> getOfficesByCountry(Integer countryId) {
        //return officeRepository.findByCountry(countryId);
        return null;
    }

    //TODO: Return dto with automapper
    //TODO: Rewrite repo
    public List<Office> getOfficesByCity(Integer cityId) {
        //return officeRepository.findByCity(cityId);
        return null;
    }

    //TODO: Return boolean
    // Work with automapped DTO
    public Office updateOffice(Integer id, Office updatedOffice) {
        Office office = getOfficeById(id);
        if (office == null) return null;
        office.setTitle(updatedOffice.getTitle());
        office.setAddress(updatedOffice.getAddress());
        return officeRepository.save(office);
    }

    public void deleteOffice(Integer id) {
        officeRepository.deleteById(id);
    }
}

