package cn.lpc.service.impl;

import cn.lpc.entity.UserMessage;
import cn.lpc.mapper.UserMessageMapper;
import cn.lpc.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {
}
