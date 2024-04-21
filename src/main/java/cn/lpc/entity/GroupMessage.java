package cn.lpc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("group_message")
public class GroupMessage {
    /**
     * 消息类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 群名
     */
    @TableField(value = "groupnickname")
    private String groupnickname;


    /**
     * 发送者
     */
    @TableField(value = "sendNickname")
    private String sendNickname;


    /**
     * 消息内容
     */
    @TableField(value = "messages")
    private String messages;

    /**
     * 发送时间
     */
    @TableField(value = "sendTime")
    private Date sendTime;
}
