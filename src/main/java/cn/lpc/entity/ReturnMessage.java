package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 该类用于返回相应的错误信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnMessage {
    /**
     * 类型
     */
    private String type;
    /**
     * 接收者
     */
    private String receiveNickname;
    /**
     * 消息
     */
    private Object messagesSort;

}
