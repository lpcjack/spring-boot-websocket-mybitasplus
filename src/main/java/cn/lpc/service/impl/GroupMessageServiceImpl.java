package cn.lpc.service.impl;

import cn.lpc.entity.GroupMessage;
import cn.lpc.entity.UserMessage;
import cn.lpc.mapper.GroupMessageMapper;
import cn.lpc.mapper.UserMessageMapper;
import cn.lpc.service.GroupMessageService;
import cn.lpc.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GroupMessageServiceImpl extends ServiceImpl<GroupMessageMapper, GroupMessage> implements GroupMessageService {
}
