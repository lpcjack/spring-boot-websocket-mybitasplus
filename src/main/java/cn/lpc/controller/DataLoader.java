package cn.lpc.controller;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Map;


//将数据从数据库中读取出来
@Component
public class DataLoader implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            String sql = "SELECT groupname, members FROM groups_table";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                String groupName = (String) row.get("groupname");
                String membersJson = (String) row.get("members");
                List<String> membersList = JSON.parseArray(membersJson, String.class);
                WebSocketController.grouplist.put(groupName, membersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
