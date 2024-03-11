package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Groups {
    /**
     * 群聊id
     */
    private Integer groupid;
    /**
     * 群聊名称
     */
    private String groupnickname;
}
