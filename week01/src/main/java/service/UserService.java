package service;

import com.sun.tools.javac.util.Assert;
import common.BusinessException;
import repository.dao.UserDao;
import repository.domain.User;

public class UserService {

    private UserService(){}
    private static UserService instance = new UserService();
    public static UserService getInstance(){
        return instance;
    }

    private UserDao userDao = UserDao.getInstance();


    public Boolean registerUser(User user){
        Assert.checkNonNull(user,"用户信息不能为空");
        Assert.checkNonNull(user.getPassword(),"用户信息不能为空");
        Assert.checkNonNull(user.getUserName(),"用户信息不能为空");
        if (userDao.isExist(user)){
            throw new BusinessException("当前用户已存在");
        }
        return userDao.register(user);
    }
}
