package lt.techin.kristina.bookingapi.repository;

import lt.techin.kristina.bookingapi.model.Visit;
import lt.techin.kristina.bookingapi.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllBySpecialistId(Long specialistId);

    List<Visit> findAllBySpecialistIdOrderByReservationTime(Long specialistId);

    List<Visit> findAllByVisitStatusInOrderByReservationTime(VisitStatus... visitStatuses);

    List<Visit> findAllByCustomerId(Long customerId);

    boolean existsByReservationCode(String reservationCode);

    Optional<Visit> findByReservationCode(String reservationCode);
}
