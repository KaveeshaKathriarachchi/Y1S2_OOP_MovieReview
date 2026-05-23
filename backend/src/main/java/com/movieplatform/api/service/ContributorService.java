package com.movieplatform.api.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ContributorService {
    private final ContributorRepository contributorRepository;

    public ContributorService(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
    }

    public Contributor add(Contributor contributor) {
        if (contributor.getId() == null || contributor.getId().isBlank()) {
            contributor.setId(generateId(contributor.getRole()));
        }
        return contributorRepository.save(contributor);
    }

    public List<Contributor> all() {
        return contributorRepository.findAll();
    }

    public Contributor update(String id, Contributor request) {
        Contributor existing = contributorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contributor not found"));
        existing.setName(request.getName());
        existing.setRole(request.getRole());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setCountry(request.getCountry());
        existing.setPhoto(request.getPhoto());
        existing.setDescription(request.getDescription());
        existing.setAwards(request.getAwards());
        existing.setNotableWorks(request.getNotableWorks());
        return contributorRepository.save(existing);
    }

    public void delete(String id) {
        if (!contributorRepository.existsById(id)) {
            throw new IllegalArgumentException("Contributor not found");
        }
        contributorRepository.deleteById(id);
    }

    private String generateId(String role) {
        String prefix = "C";
        if ("Actor".equalsIgnoreCase(role)) prefix = "A";
        if ("Director".equalsIgnoreCase(role)) prefix = "D";
        return prefix + String.format("%03d", contributorRepository.count() + 1);
    }
}
