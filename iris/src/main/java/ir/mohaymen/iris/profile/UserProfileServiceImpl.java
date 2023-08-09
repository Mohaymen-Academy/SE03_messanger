package ir.mohaymen.iris.profile;

import ir.mohaymen.iris.user.User;
import ir.mohaymen.iris.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Override
    public Iterable<UserProfile> getAll() {
        return userProfileRepository.findAll();
    }

    @Override
    public UserProfile getById(Long id) {
        return userProfileRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<UserProfile> getByUser(User user) {
        return userProfileRepository.findByUser(user);
    }

    @Override
    public UserProfile createOrUpdate(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public void deleteById(Long id) {
        userProfileRepository.deleteById(id);
    }

    public Iterable<UserProfile> getByUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return getByUser(user);
    }
}
