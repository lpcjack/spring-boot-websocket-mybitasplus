package cn.lpc.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FriendsLoader implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //将用户好友的信息回滚到friendlist中
    @Override
    public void run(ApplicationArguments arg) throws Exception{
        try {
            String sql1 = "SELECT username, friends FROM friends_table";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql1);
            for (Map<String, Object> row : rows) {
                String username = (String) row.get("username");
                String FriendsJson = (String) row.get("friends");
                List<String> friendsList = JSON.parseArray(FriendsJson, String.class);
                WebSocketController.friendlist.put(username, friendsList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
