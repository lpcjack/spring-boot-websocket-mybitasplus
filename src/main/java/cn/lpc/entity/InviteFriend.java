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
public class InviteFriend {
    /**
     * 类型
     */
    private String type;

    /**
     * 邀请者
     */
    private String Inviter;

    /**
     * 群聊名
     */
    private String groupName;

    /**
     * 选择的好友
     */
    private List<String> messages;
}
