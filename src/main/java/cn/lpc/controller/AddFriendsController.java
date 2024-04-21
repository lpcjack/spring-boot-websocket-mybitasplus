package cn.lpc.controller;

import cn.lpc.entity.ReturnMessage;
import cn.lpc.mapper.FriendsMapper;
import cn.lpc.service.FriendsService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173")
public class AddFriendsController {
    private final FriendsMapper friendsMapper;
    private final JdbcTemplate jdbcTemplate;




    @Autowired
    public AddFriendsController(FriendsMapper friendsMapper, JdbcTemplate jdbcTemplate) {
        this.friendsMapper = friendsMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    //添加好友
    public void addFriends(String username, String friendName){

        //检查一下是否存在该好友，确保唯一性
        String reapt = repeatInspect(username, friendName);

        //先从数据库中查询是否存在对应用户，存在可以进行下一步操作
        String result1 = SelectFriend(friendName);

        // 首先找到这个用户
        if ("200".equals(result1) ){
            //数据库插入
            if ("insert".equals(reapt)){


                List<String> friends = new ArrayList<>();
                friends.add(friendName);
                WebSocketController.addFriends(username, friends);
                //存储到数据库
                SaveFriendsToDataBase(username, friends);
                // 更新好友的好友列表
                UpdateFriendsFriendList(friendName, username);

                log.info("数据库插入成功！！");

            }
            //数据库更新
            else if("update".equals(reapt)){

                List<String> friends = WebSocketController.friendlist.get(username);
                friends.add(friendName);
                WebSocketController.addFriends(username, friends);
                //更新数据库
                UpdateFriendsList(username, friends);
                // 更新好友的好友列表
                UpdateFriendsFriendList(friendName, username);
                log.info("数据库更新成功！！");

            }


        }
        //没有找到该用户
        else {
            log.info("没有找到该用户！！");
            WebSocketController.sendP2PChatMessage(username, JSON.toJSONString(ReturnMessage.builder().type("AddErr").receiveNickname(username).messagesSort("noPerson").build()));
        }
    }
    @PostMapping("/searchFD")
    public String searchFD(@RequestBody JSONObject jsonObject){
        String friendName =jsonObject.getString("friendName");
        String userName = jsonObject.getString("userName");
        List<String> list = WebSocketController.friendlist.get(userName);
        if(list != null && list.contains(friendName)){
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type", "Found");
            jsonObject1.put("friendName", friendName);
            jsonObject1.put("userName", userName);

            return jsonObject1.toString();
        }
        else{
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type", "notFound");

            return jsonObject1.toString();
        }
    }

    //查找好友信息
    @PostMapping("/searchFriend")
    public String search(@RequestBody JSONObject jsonObject){
        // 打印结果
        log.info(jsonObject.toString());

        // 用户姓名
        String username = jsonObject.getString("username");
        //好友姓名
        String friendName = jsonObject.getString("friendName");

        // 检测是否为搜索自己
        if (username.equals(friendName)){
            // 禁止搜索自己
            return "ban";
        }
        // 不是搜索自己
        else{
            //接收查找结果
            String result = SelectFriend(friendName);
            if (result.equals("200")){
                //找到了，返回json字符串
                return jsonObject.toString();
            }else {
                //否则没找到
                return "NotFound";
            }
        }

    }

    //插入存储到数据库
    private void SaveFriendsToDataBase(String username, List<String> friends){
        try {
            String membersJson = JSON.toJSONString(friends);
            jdbcTemplate.update("INSERT INTO friends_table (username, friends) VALUES (?, ?)", username, membersJson);
            log.info("好友资料存储成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新数据库已有信息
    private synchronized void UpdateFriendsList(String username, List<String> friends){
        try {
            String friendsList = JSON.toJSONString(friends);
            jdbcTemplate.update("UPDATE friends_table SET friends = ? WHERE username = ? ", friendsList, username);
            log.info("好友资料更新成功成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //更新好友的好友列表
    private void UpdateFriendsFriendList(String friendName, String username){
        // 如果好友信息存储中有该好友的列表
        if(WebSocketController.friendlist.containsKey(friendName)){
            List<String> friend = WebSocketController.friendlist.get(friendName);
            if (friend.contains(username)){
                log.info("不可重复添加该好友："+username);
            }
            // 如果不包括
            else {
                friend.add(username);
                WebSocketController.friendlist.put(friendName, friend);
                // 更新数据库内容
                UpdateFriendsList(friendName, friend);

            }

        }
        // 不存在该好友的好友列表
        else {
            List<String> list = new ArrayList<>();
            list.add(username);
            WebSocketController.friendlist.put(friendName, list);
            SaveFriendsToDataBase(friendName, list);
        }

    }



    //数据库查询是否存在该用户
    //直接从好友列表中查找用户
    private  String SelectFriend(String name){
        String result = friendsMapper.selectFriend(name);
        if (result != null){
            // 找到该用户
            log.info("搜索到的结果是："+result);
            return "200";
        }
        // 未找到用户
        else {
            log.info("未找到该用户！！");
            return "404";
        }
    }


    //判断是插入还是更新
    private String repeatInspect(String username, String friendName){
        //读取相应信息
        List<String> list = WebSocketController.friendlist.get(username);
        //先判断一下是否存在该用户用户
        //如果存在将好友列表读取出来
        if (WebSocketController.friendlist.containsKey(username) && list != null){
            //如果已经存在该好友，提示不能添加
            if(list.contains(friendName)){
                log.info("已经存在该好友，不可添加！！");
                return "failure";
            }
            //如果不存在，可以添加
            //更新数据库
            else {
                log.info("更新数据库！！");
                return "update";
            }
        }
        //不存在该用户的列表
        //可以插入信息
        else{
            log.info("数据插入数据库！！");
            return "insert";
        }

    }


}



