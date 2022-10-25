package com.example.spring_demo.service;

import com.example.spring_demo.exception.UserAlreadyExistsException;
import com.example.spring_demo.model.User;
import com.example.spring_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Log
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User saveUser(User user) {
    if (checkIfUserExist(user)) {
      throw new UserAlreadyExistsException("User with this credentials already exist in database!!!");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Optional<User> findById(int id) {
    return userRepository.findById(id);
  }

  public void deleteById(int id) {
    Optional<User> optionalUser = userRepository.findById(id);

    if (optionalUser.isPresent()) {
      userRepository.deleteById(id);
    }
  }

  public boolean checkIfUserExist(User user) {
    String email = user.getEmail();
    String userName = user.getUsername();
    boolean userExist = false;
    Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
    Optional<User> optionalUser1 = Optional.ofNullable(userRepository.findByUsername(userName));

    if (optionalUser.isPresent() || optionalUser1.isPresent()) {
      userExist = true;
    }
    return userExist;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      log.info("User not found!!" + username);
    } else {
      log.info("User found!!" + username);
    }
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    assert user != null;
    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
  }
}
