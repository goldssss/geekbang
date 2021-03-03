package repository.dao;

import repository.domain.User;

public class UserDao {

    private UserDao(){}
    private static UserDao instance = new UserDao();
    public static UserDao getInstance(){
        return instance;
    }

    public Boolean register(User user){
        return true;
    }

    public Boolean isExist(User user){
        return true;
    }

}
