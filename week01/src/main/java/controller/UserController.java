package controller;


import com.sun.tools.javac.util.Assert;
import mvc.Controller;
import mvc.RequestMapping;
import repository.domain.User;
import service.UserService;


@Controller
public class UserController {

    private UserService userService = UserService.getInstance();

    @RequestMapping("/user/register")
    public String registerUser(User user){
        Assert.checkNonNull(user,"用户信息不能为空");
        Assert.checkNonNull(user.getPassword(),"用户信息不能为空");
        Assert.checkNonNull(user.getUserName(),"用户信息不能为空");

        Boolean result = userService.registerUser(user);

        return result?"success":"error";
    }
}
