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
@TableName("message")
public class UserMessage {

    /**
     * 消息类型
     */
    @TableField(value = "messageType")
    private String messageType;
    /**
     * 发送者
     */
    @TableField(value = "sender")
    private String sender;

    /**
     * 接收者
     */
    @TableField(value = "receiver")
    private String receiver;

    /**
     * 消息内容
     */
    @TableField(value = "message")
    private String message;

    /**
     * 发送时间
     */
    @TableField(value = "sendtime")
    private Date sendtime;
}
