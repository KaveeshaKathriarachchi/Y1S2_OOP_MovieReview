package movierental.repository;

import movierental.model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository
        extends JpaRepository<Contributor, String> {
}
