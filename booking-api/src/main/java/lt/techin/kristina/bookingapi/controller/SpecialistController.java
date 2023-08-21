package lt.techin.kristina.bookingapi.controller;

import lt.techin.kristina.bookingapi.model.Specialist;
import lt.techin.kristina.bookingapi.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {

    @Autowired
    private SpecialistService specialistService;

    @GetMapping
    public ResponseEntity<List<Specialist>> getAllSpecialists() {
        return ResponseEntity.ok(specialistService.getAllSpecialists());
    }
}
