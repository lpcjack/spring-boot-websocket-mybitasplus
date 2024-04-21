package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFriend {
    /**
     * 类型
     */
    private String type;
    /**
     * 请求者
     */
    private String sendNickname;
    /**
     * 消息
     */
    private Object messages;

    /**
     * 状态
     */
    private boolean status;
}
