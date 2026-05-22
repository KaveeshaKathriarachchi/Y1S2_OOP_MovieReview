package movierental.service;

import movierental.model.User;
import movierental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User authenticate(String username, String password) {

        return repo.findByUsernameAndPassword(username, password);
    }
}