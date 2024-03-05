package cn.lpc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Messages {
    @TableId(type = IdType.AUTO)
    private Integer numid;
    /**
     * 消息类型
     */
    @TableField(value = "type")
    private String type;
    /**
     * 发送好友昵称
     */
    @TableField(value = "sender")
    private String sendNickname;
    /**
     * 接收好友昵称
     */
    @TableField(value = "receiver")
    private String receiveNickname;
    /**
     * 消息内容
     */
    @TableField(value = "message")
    private Object messages;
    /**
     * 所属群聊名
     */
    private String groupnickname;

    /**
     * 图片数据
     */

    /**
     * 判断登录是否成功
     */
    private boolean loginSuccess;

}
