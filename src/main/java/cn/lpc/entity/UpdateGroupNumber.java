package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupNumber {
    /**
     * 类型
     */
    private String type;

    /**
     * 接收者
     */
    private String receiver;

    /**
     * 群名
     */
    private String groupName;

    /**
     * 群聊长度
     */
    private String groupLength;
}
