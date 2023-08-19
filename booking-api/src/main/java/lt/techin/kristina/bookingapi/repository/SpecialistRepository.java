package lt.techin.kristina.bookingapi.repository;

import lt.techin.kristina.bookingapi.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
}
