package com.jishnu.sample.service;

import com.jishnu.sample.model.User;

import java.util.List;

public interface UserService {

    public User findUserByEmail(String email);

    public void saveUser(User user);
    public List<User> getAllUser();
}
