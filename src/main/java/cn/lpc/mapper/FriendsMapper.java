package cn.lpc.mapper;

import cn.lpc.entity.Friends;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FriendsMapper extends BaseMapper<Friends> {
    // 登陆验证
    @Select("SELECT * FROM friends WHERE nickname = #{nickname} AND password = #{password}")
    String selectNickname(@Param("nickname") String nickanme , @Param("password") String password);

    // 修改密码
    @Update("UPDATE friends SET password = #{enablepassword} WHERE nickname = #{nickname}")
    void revisePsw(@Param("nickname") String nickname ,@Param("enablepassword") String enablepassword);

    //查找是否存在这个人
    @Select("SELECT * FROM friends WHERE nickname = #{nickname}")
    String selectFriend(@Param("nickname") String nickname);

    // 获取用户id
    @Select("SELECT pid FROM friends WHERE nickname = #{nickname} AND password = #{password}")
    String selectUserId(@Param("nickname") String nickname , @Param("password") String password);
}
