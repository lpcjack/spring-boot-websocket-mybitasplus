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
public class Group {
    /**
     * 类型
     */
    private String type;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 群名
     */
    private String groupName;

    /**
     * 群成员
     */
    private List<String> messages;
}
