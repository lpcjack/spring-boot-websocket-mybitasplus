package cn.lpc.service.impl;

import cn.lpc.entity.Friends;
import cn.lpc.mapper.FriendsMapper;
import cn.lpc.service.FriendsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FriendsServiceImpl  extends ServiceImpl<FriendsMapper, Friends> implements FriendsService {
}
