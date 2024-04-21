package cn.lpc.controller;

import cn.lpc.entity.Friends;
import cn.lpc.mapper.FriendsMapper;
import cn.lpc.service.FriendsService;
import cn.lpc.util.VerifyCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Slf4j
@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class FriendController {

    @Autowired
    private FriendsMapper friendsMapper;

    @Autowired
    private FriendsService friendsService;



    //注册
    @PostMapping("/sign")
    public String addUser(@RequestBody JSONObject jsonObject){
        try {
            String nickname = jsonObject.getString("nickname");
            String password = jsonObject.getString("password");

            // 判断是否重名
            // 数据库中不存在
            if(friendsMapper.selectFriend(nickname) == null){
                Friends friends=new Friends();
                friends.setNickname(jsonObject.getString("nickname"));
                friends.setPassword(jsonObject.getString("password"));
                // 存储账号密码
                friendsService.save(friends);

                // 获取用户id
                String userId = friendsMapper.selectUserId(nickname, password);
                // 组装信息存储
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", "success");
                jsonObject1.put("userId", userId);
                jsonObject1.put("nickname", nickname);
                jsonObject1.put("password", password);

                // 返回数据
                return jsonObject1.toString();
            }
            else {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", "repeat");

                // 返回数据
                return jsonObject1.toString();
            }
        } catch (Exception e) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type", "error");
            e.printStackTrace();
            return jsonObject1.toString();
        }

    }

    //登陆验证
    @PostMapping("/login")
    public String login(HttpServletRequest request,@RequestBody JSONObject jsonObject){
        //获取用户账号与密码
        String nickname = jsonObject.getString("nickname");
        String password = jsonObject.getString("password");
        String vericode = jsonObject.getString("verifycode");
        // 验证码转小写
        vericode = vericode.toLowerCase();

        log.info("输入的验证码为："+vericode);

        HttpSession session = request.getSession(false);  // 不创建新会话
        if (session == null) {
            log.error("未找到会话");
            return "Session not found";
        }
        String storedVerifyCode = (String) session.getAttribute("verifyCode");
        storedVerifyCode = storedVerifyCode.toLowerCase();
        log.info("从Session ID: " + session.getId() + " 取得验证码"+storedVerifyCode);

        //数据库读取是否存在
        String user = friendsMapper.selectNickname(nickname , password);
        // 用户id
        String userId = friendsMapper.selectUserId(nickname, password);

        if (user != null){
            if(vericode.equals(storedVerifyCode)){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", "success");
                jsonObject1.put("nickname", nickname);
                jsonObject1.put("password", password);
                jsonObject1.put("userID", userId);
                return jsonObject1.toString();
            }
            //验证码不一致
            else {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", "inconsistent");
                return jsonObject1.toString();
            }
        }
        // 找不到该用户
        else{
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type", "NotFind");
            return jsonObject1.toString();
        }
    }

    //修改密码
    @PostMapping("/revise")
    public String revise(@RequestBody JSONObject requestBody) {
        try {
            // 获取需要的字段
            String nickname = requestBody.getString("nickname");
            // 密码
            String password = requestBody.getString("password");
            // 确认密码
            String enablepassword = requestBody.getString("enablepassword");

            // 验证账号密码是否一致
            String user = friendsMapper.selectNickname(nickname, password);

            // 验证是否一致
            // 一致
            if (user != null) {
                String userId = friendsMapper.selectUserId(nickname, password);
                // 修改密码
                friendsMapper.revisePsw(nickname, enablepassword);
                // 返回json串
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "success");
                jsonObject.put("userId", userId);
                jsonObject.put("nickname", nickname);
                jsonObject.put("password", password);
                return jsonObject.toString();
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "failure");
                // 验证失败，返回相应的失败信息
                return jsonObject.toString();
            }
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "error");
            e.printStackTrace();
            return jsonObject.toString();
        }

    }


}
