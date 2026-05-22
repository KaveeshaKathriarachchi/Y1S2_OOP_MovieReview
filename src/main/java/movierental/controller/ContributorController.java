package movierental.controller;

import movierental.model.Contributor;
import movierental.service.ContributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contributors")
public class ContributorController {

    // ✅ FIX: Let Spring manage this (VERY IMPORTANT)
    @Autowired
    private ContributorService contributorService;

    // ✅ CREATE: Add a contributor

    @PostMapping("/add")
    public String addContributor(@RequestBody Contributor contributor) {
        contributorService.addContributor(contributor);
        return "Contributor added successfully";
    }

    // ✅ READ: View all contributors
    @GetMapping("/all")
    public List<Contributor> getAllContributors() {
        return contributorService.getAllContributors();
    }

    // ✅ UPDATE contributor
    @PutMapping("/update/{id}")
    public String updateContributor(
            @PathVariable String id,
            @RequestBody Contributor contributor) {

        boolean updated = contributorService.updateContributor(id, contributor);

        return updated ? "Contributor updated successfully"
                : "Contributor not found";
    }

    // ✅ DELETE contributor
    @DeleteMapping("/delete/{id}")
    public String deleteContributor(@PathVariable String id) {

        boolean deleted = contributorService.deleteContributor(id);

        return deleted ? "Contributor deleted successfully"
                : "Contributor not found";
    }
}
