package own.savage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import own.savage.dao.UserDAO;
import own.savage.entity.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;

    public UserService(@Autowired UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Transactional
    public Optional<User> findById(Long id) {
        return userDAO.findById(id);
    }

    @Transactional
    public User save(User user) {
        return userDAO.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }
}
