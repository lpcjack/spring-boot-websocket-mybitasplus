package cn.lpc.mapper;

import cn.lpc.entity.Messages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessagesMapper extends BaseMapper<Messages> {
    @Insert("INSERT INTO message (sender, receiver, message, type) VALUES (#{sendNickname}, #{receiveNickname}, #{messages}, #{type})")
    void insertMessage(@Param("sendNickname") String sendNickname, @Param("receiveNickname") String receiveNickname, @Param("messages") Object messages, @Param("type") String type);


}
