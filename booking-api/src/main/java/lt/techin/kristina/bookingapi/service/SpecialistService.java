package lt.techin.kristina.bookingapi.service;

import lt.techin.kristina.bookingapi.model.Specialist;
import lt.techin.kristina.bookingapi.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;

    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }
}
