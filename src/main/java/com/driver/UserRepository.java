package com.driver;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserRepository {
    static HashMap<String, User> userDb;

    public UserRepository() {
        userDb = new HashMap<>();
    }

    static public String createUser(String name, String mobile) throws Exception {
        User newUser = new User(name, mobile);
            if (!userDb.containsValue(newUser)) {
//                User newUser = new User(name, mobile);
                userDb.put(mobile, newUser);
                return "SUCCESS";
            }
                return ("User already exists");
    }
}
