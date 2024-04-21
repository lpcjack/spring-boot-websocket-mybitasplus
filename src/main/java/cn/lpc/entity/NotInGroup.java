package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotInGroup {
    /**
     * 类型
     */
    private String type;

    /**
     * 传输的信息
     */
    private Object messages;
}
