package lt.techin.kristina.bookingapi.controller;

import lt.techin.kristina.bookingapi.exception.BookingValidationException;
import lt.techin.kristina.bookingapi.model.Visit;
import lt.techin.kristina.bookingapi.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {

    @Autowired
    private VisitService visitService;

    @GetMapping
    public ResponseEntity<List<Visit>> getUpcomingVisits() {
        return ResponseEntity.ok(visitService.getRelevantVisits());
    }

    @GetMapping("/{specialistId}")
    public ResponseEntity<List<Visit>> getVisitsBySpecialist(@PathVariable Long specialistId) {
        if (specialistId == null) {
            throw new BookingValidationException("Specialist id cannot be null");
        }
        return ResponseEntity.ok(visitService.getRelevantVisitsBySpecialist(specialistId));
    }

    @PostMapping("/create/{specialistId}")
    public ResponseEntity<Visit> createVisit(@PathVariable Long specialistId, @RequestParam String email) {
        if (specialistId == null) {
            throw new BookingValidationException("Specialist id cannot be null");
        }
        if (email == null || email.equals("")) {
            throw new BookingValidationException("Customer email cannot be null or empty");
        }
        return ResponseEntity.ok(visitService.createVisitForSpecialist(specialistId, email));
    }

    @GetMapping("/get/{reservationCode}")
    public ResponseEntity<Visit> getVisit(@PathVariable String reservationCode) {
        if (reservationCode == null || reservationCode.equals("")) {
            throw new BookingValidationException("Reservation code cannot be null");
        }
        return ResponseEntity.ok(visitService.getVisit(reservationCode));
    }

    @PatchMapping("/cancel/{reservationCode}")
    public ResponseEntity<Visit> cancelVisit(@PathVariable String reservationCode, @RequestParam String email) {
        if (reservationCode == null || reservationCode.equals("")) {
            throw new BookingValidationException("Reservation code cannot be null");
        }
        if (email == null || email.equals("")) {
            throw new BookingValidationException("Customer email cannot be null or empty");
        }
        return ResponseEntity.ok(visitService.cancelVisit(reservationCode, email));
    }

    @PatchMapping("/{specialistId}/start/{visitId}")
    public ResponseEntity<Visit> startVisit(@PathVariable Long specialistId, @PathVariable Long visitId) {
        if (specialistId == null) {
            throw new BookingValidationException("Specialist id cannot be null");
        }
        if (visitId == null) {
            throw new BookingValidationException("Visit id cannot be null");
        }
        return ResponseEntity.ok(visitService.startVisit(specialistId, visitId));
    }

    @PatchMapping("/{specialistId}/end/{visitId}")
    public ResponseEntity<Visit> endVisit(@PathVariable Long specialistId, @PathVariable Long visitId) {
        if (specialistId == null) {
            throw new BookingValidationException("Specialist id cannot be null");
        }
        if (visitId == null) {
            throw new BookingValidationException("Visit id cannot be null");
        }
        return ResponseEntity.ok(visitService.endVisit(specialistId, visitId));
    }
}
