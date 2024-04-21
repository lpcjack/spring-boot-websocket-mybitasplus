package cn.lpc.controller;

import cn.lpc.entity.Groups;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173")
public class CreateGroupController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CreateGroupController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/create")
    public String createGroup(@RequestBody JSONObject jsonObject){
        //获取群名
        String groupname = jsonObject.getString("groupname");
        //将群名存储到grouplist
        Groups groups = new Groups();
        groups.setGroupnickname(groupname);
        WebSocketController.groupsList.add(groups);

        //获取群成员
        JSONArray membersArray = jsonObject.getJSONArray("members");
        List<String> membersList = new ArrayList<>();
        for (Object obj : membersArray) {
            membersList.add(obj.toString());
        }

        //存储群名以及对应群成员
        WebSocketController.createGroup(groupname, membersList);

        // 存储到数据库
        saveGroupToDatabase(groupname, membersList);
        // 打印List内容
        System.out.println("成员列表:");
        for (String member : membersList) {
            System.out.println(member);
        }
        System.out.println(jsonObject);

        //返回群聊信息给客户端
        return jsonObject.toString();
    }
    public void saveGroupToDatabase(String groupName, List<String> membersArray) {
        try {
            String membersJson = JSON.toJSONString(membersArray);
            jdbcTemplate.update("INSERT INTO groups_table (groupname, members) VALUES (?, ?)", groupName, membersJson);
            System.out.println("群聊数据存储成功！！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 更新群组数据库
    public void updateGroupToDatabase(String groupName, List<String> membersArray) {
        try {
            String membersJson = JSON.toJSONString(membersArray);
            jdbcTemplate.update("UPDATE groups_table SET members = ? WHERE groupname = ?", membersJson, groupName);
            System.out.println("群聊数据存储成功！！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
