package movierental.service;

import movierental.model.Contributor;
import movierental.repository.ContributorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContributorService {

    @Autowired
    private ContributorRepository repo;

    // ✅ AUTO ID GENERATOR
    private String generateContributorId(String role) {

        long count = repo.count() + 1;
        String number = String.format("%03d", count);

        if ("Actor".equalsIgnoreCase(role)) {
            return "A" + number;
        } else if ("Director".equalsIgnoreCase(role)) {
            return "D" + number;
        } else {
            return "C" + number;
        }
    }

    // ✅ CREATE
    public void addContributor(Contributor contributor) {

        if (contributor.getId() == null || contributor.getId().isEmpty()) {
            String id = generateContributorId(contributor.getRole());
            contributor.setId(id);
        }

        repo.save(contributor);
    }

    // ✅ READ
    public List<Contributor> getAllContributors() {
        return repo.findAll();
    }

    // ✅ ✅ FIXED UPDATE (IMPORTANT)
    public boolean updateContributor(String id, Contributor updatedContributor) {

        Optional<Contributor> optionalContributor = repo.findById(id);

        if (optionalContributor.isEmpty()) {
            return false;
        }

        Contributor existing = optionalContributor.get();

        // ✅ update ALL fields safely
        existing.setName(updatedContributor.getName());
        existing.setRole(updatedContributor.getRole());
        existing.setDateOfBirth(updatedContributor.getDateOfBirth());
        existing.setCountry(updatedContributor.getCountry());
        existing.setPhoto(updatedContributor.getPhoto());

        // ✅ NEW FIELDS
        existing.setDescription(updatedContributor.getDescription());
        existing.setAwards(updatedContributor.getAwards());
        existing.setNotableWorks(updatedContributor.getNotableWorks());

        repo.save(existing);

        return true;
    }

    // ✅ DELETE
    public boolean deleteContributor(String id) {

        if (!repo.existsById(id)) {
            return false;
        }

        repo.deleteById(id);
        return true;
    }
}