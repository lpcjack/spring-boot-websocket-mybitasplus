package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messages {
    /**
     * 消息类型
     */
    private String type;
    /**
     * 发送好友昵称
     */
    private String sendNickname;
    /**
     * 接收好友昵称
     */
    private String receiveNickname;
    /**
     * 消息内容
     */
    private Object messages;
    /**
     * 所属群聊名
     */
    private String groupnickname;

    /**
     * 存储群聊人数
     */
    private Object groupNumber;

}
