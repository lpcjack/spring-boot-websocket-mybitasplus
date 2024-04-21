package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGroup {
    /**
     * 类型
     */
    private String type;

    /**
     * 群聊名称
     */
    private String groupName;

    /**
     * 群聊长度
     */
    private String groupLength;

    /**
     * 消息（群成员）
     */
    private List<String> member;

}
