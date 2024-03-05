package cn.lpc.controller;

import cn.lpc.entity.Friends;
import cn.lpc.mapper.FriendsMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173")
public class FriendController {

    @Autowired
    private FriendsMapper friendsMapper;

    //注册
    @PostMapping("/sign")
    public String addUser(@RequestBody String nickname){
        Friends friends1 = JSON.parseObject(nickname , Friends.class);
        Friends friends=new Friends();
        friends.setNickname(friends1.getNickname());
        friends.setPassword(friends1.getPassword());
        friendsMapper.insert(friends);

        return "successful";

    }

    //登陆验证
    @PostMapping("/login")
    public String login(@RequestBody Friends friends){
        //获取用户账号与密码
        String nickname = friends.getNickname();
        String password = friends.getPassword();

        //验证是否一致
        String user = friendsMapper.selectNickname(nickname , password);
        System.out.println(user);
        // 验证是否一致
        if (user != null) {
            // 验证通过，执行登录成功的操作
            // 例如返回一个成功的响应或者设置用户登录状态等
            return "login success";
        } else {
            // 验证失败，返回相应的失败信息
            return "login failed";
        }

    }

    //修改密码
    @PostMapping("/revise")
    public String revise(@RequestBody JSONObject requestBody) {
        // 获取需要的字段
        String nickname = requestBody.getString("nickname");
        String password = requestBody.getString("password");
        String enablepassword = requestBody.getString("enablepassword");

        // 验证是否一致
        String user = friendsMapper.selectNickname(nickname, password);

        // 验证是否一致
        if (user != null) {
            friendsMapper.revisePsw(nickname, enablepassword);
            return "revise success";
        } else {
            // 验证失败，返回相应的失败信息
            return "revise failed";
        }
    }


}
