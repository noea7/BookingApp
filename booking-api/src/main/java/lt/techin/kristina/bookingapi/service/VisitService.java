package lt.techin.kristina.bookingapi.service;

import lt.techin.kristina.bookingapi.exception.BookingValidationException;
import lt.techin.kristina.bookingapi.model.Customer;
import lt.techin.kristina.bookingapi.model.Specialist;
import lt.techin.kristina.bookingapi.model.Visit;
import lt.techin.kristina.bookingapi.model.VisitStatus;
import lt.techin.kristina.bookingapi.repository.CustomerRepository;
import lt.techin.kristina.bookingapi.repository.SpecialistRepository;
import lt.techin.kristina.bookingapi.repository.VisitRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {

    @Value("${visit.duration.minutes}")
    private Integer visitDuration;

    @Value("${reservation.code.length}")
    private Integer reservationCodeLength;

    @Value("${upcoming.visit.number}")
    private Integer upcomingVisitNumber;

    @Value("${work.start.time}")
    private Integer workStartTime;

    @Value("${work.end.time}")
    private Integer workEndTime;

    @Value("${work.break.time}")
    private Integer workBreakTime;

    @Value("${working.on.saturday:false}")
    private Boolean isWorkingOnSaturday;

    @Value("${working.on.sunday:false}")
    private Boolean isWorkingOnSunday;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Visit> getRelevantVisits() {
        List<Visit> upcomingVisits = new LinkedList<>(visitRepository
                .findAllByVisitStatusInOrderByReservationTime(VisitStatus.IN_PROGRESS));
        upcomingVisits.addAll(visitRepository.findAllByVisitStatusInOrderByReservationTime(VisitStatus.PENDING)
                .stream().limit(upcomingVisitNumber).collect(Collectors.toList()));
        return upcomingVisits;
    }

    public List<Visit> getRelevantVisitsBySpecialist(Long specialistId) {
        if (!specialistRepository.existsById(specialistId)) {
            throw new BookingValidationException("Specialist with id " + specialistId + " not found");
        }
        return visitRepository.findAllBySpecialistIdOrderByReservationTime(specialistId).stream()
                .filter(visit -> (visit.getVisitStatus().equals(VisitStatus.PENDING) ||
                visit.getVisitStatus().equals(VisitStatus.IN_PROGRESS))).collect(Collectors.toList());
    }

    public Visit getVisit(String reservationCode) {
        return visitRepository.findByReservationCode(reservationCode).orElseThrow(() ->
                new BookingValidationException("Booking with reservation code " + reservationCode + " does not exist"));
    }

    public Visit createVisitForSpecialist(Long specialistId, String email) {
        Specialist specialist = specialistRepository.findById(specialistId).orElse(null);
        if (specialist == null) {
            throw new BookingValidationException("Specialist with id " + specialistId + " not found");
        }
        Customer customer = customerRepository.findByEmail(email).orElse(new Customer(email));
        if (customer.getId() == null) {
            customerRepository.save(customer);
        }
        Visit visit = new Visit();
        visit.setSpecialist(specialist);
        visit.setCustomer(customer);
        visit.setVisitStatus(VisitStatus.PENDING);
        visit.setReservationCode(generateReservationCode());
        visit.setReservationTime(generateReservationTime(specialist, customer));
        return visitRepository.save(visit);
    }

    public Visit cancelVisit(String reservationCode, String email) {
        Visit visit = visitRepository.findByReservationCode(reservationCode).orElseThrow(() ->
                new BookingValidationException("Booking with reservation code " + reservationCode + " does not exist"));
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() ->
                new BookingValidationException("Customer with email " + email + " does not exist"));
        if (visit.getCustomer().equals(customer)) {
            visit.setVisitStatus(VisitStatus.CANCELLED);
            return visitRepository.save(visit);
        } else {
            throw new BookingValidationException("Cannot cancel other customers' visits");
        }
    }

    public Visit startVisit(Long specialistId, Long visitId) {
        Visit visit = visitRepository.findById(visitId).orElseThrow(() ->
                new BookingValidationException("Booking with id " + visitId + " does not exist"));
        Specialist specialist = specialistRepository.findById(specialistId).orElseThrow(() ->
                new BookingValidationException("Specialist with id " + specialistId + " does not exist"));
        if (!visit.getSpecialist().equals(specialist)) {
            throw new BookingValidationException("Cannot start other specialists' visits");
        }
        if (!visit.getVisitStatus().equals(VisitStatus.PENDING)) {
            throw new BookingValidationException("Invalid visit status");
        }
        List<Visit> existingSpecialistVisits = visitRepository.findAllBySpecialistId(specialistId);
        if (existingSpecialistVisits.stream().anyMatch(existingVisit -> existingVisit.getVisitStatus()
                .equals(VisitStatus.IN_PROGRESS))) {
            throw new BookingValidationException("Specialist can only have one active visit at a time");
        }
        visit.setVisitStatus(VisitStatus.IN_PROGRESS);
        return visitRepository.save(visit);
    }

    public Visit endVisit(Long specialistId, Long visitId) {
        Visit visit = visitRepository.findById(visitId).orElseThrow(() ->
                new BookingValidationException("Booking with id " + visitId + " does not exist"));
        Specialist specialist = specialistRepository.findById(specialistId).orElseThrow(() ->
                new BookingValidationException("Specialist with id " + specialistId + " does not exist"));
        if (!visit.getSpecialist().equals(specialist)) {
            throw new BookingValidationException("Cannot start other specialists' visits");
        }
        if (!visit.getVisitStatus().equals(VisitStatus.IN_PROGRESS)) {
            throw new BookingValidationException("Invalid visit status");
        }
        visit.setVisitStatus(VisitStatus.COMPLETED);
        return visitRepository.save(visit);
    }

    private LocalDateTime generateReservationTime(Specialist specialist, Customer customer) {
        List<Visit> plannedSpecialistVisits = visitRepository.findAllBySpecialistId(specialist.getId()).stream()
                .filter(visit -> !visit.getVisitStatus().equals(VisitStatus.CANCELLED)).collect(Collectors.toList());
        List<Visit> existingCustomerVisits = visitRepository.findAllByCustomerId(customer.getId()).stream()
                .filter(visit -> !visit.getVisitStatus().equals(VisitStatus.CANCELLED)).collect(Collectors.toList());
        LocalDateTime closestTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int currentMinute = closestTime.getMinute();
        if (currentMinute % visitDuration != 0) {
            closestTime = closestTime.plusMinutes(visitDuration - (currentMinute % visitDuration));
        }

        for (LocalDateTime i = closestTime; true; i = i.plusMinutes(visitDuration)) {
            if (i.getHour() < workStartTime || i.getHour() >= workEndTime || i.getHour() == workBreakTime) {
                continue;
            }
            if ((i.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !isWorkingOnSaturday) ||
                    (i.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !isWorkingOnSunday)) {
                continue;
            }

            LocalDateTime visitTime = i;
            if (plannedSpecialistVisits.stream().noneMatch(visit -> visit.getReservationTime().equals(visitTime)) &&
                existingCustomerVisits.stream().noneMatch(visit -> visit.getReservationTime().equals(visitTime))) {
                return visitTime;
            }
        }
    }

    private String generateReservationCode() {
        String reservationCode = RandomStringUtils.randomAlphanumeric(reservationCodeLength);
        if (visitRepository.existsByReservationCode(reservationCode)){
            return generateReservationCode();
        }
        return reservationCode;
    }
}
