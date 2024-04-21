package cn.lpc.controller;

import cn.lpc.entity.*;
import cn.lpc.service.GroupMessageService;
import cn.lpc.service.UserMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.github.houbb.sensitive.word.core.SensitiveWord;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.WebSession;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{nickname}")
public class WebSocketController {

    // 调用创建群聊类
    private static CreateGroupController createGroupController;
    @Autowired
    public void setAddFriendsToGroup(CreateGroupController createGroupController){
        WebSocketController.createGroupController = createGroupController;
    }

    // 调用添加好友类
    private static AddFriendsController addFriendsController;
    @Autowired
    public void setAddFriendController(AddFriendsController addFriendsController){
        WebSocketController.addFriendsController = addFriendsController;
    }



    /**
     * 创建一个缓存（存储单人聊天记录）
     */
    // mybitasplus配置类
    private static UserMessageService userMessageService;
    @Autowired
    public void setOgLocationService(UserMessageService userMessageService) {
        WebSocketController.userMessageService = userMessageService;
    }
    //
    private static final Integer LIST_SIZE = 3;
    // 私聊信息列表
    private static ArrayList<UserMessage> MessageList = new ArrayList<>();




    /**
     * 存储群聊天记录
     */
    // mybitasplus配置类
    private static GroupMessageService groupMessageService;
    @Autowired
    public void setGroupMessageStore(GroupMessageService groupMessageService) {
        WebSocketController.groupMessageService = groupMessageService;
    }
    private static final Integer LIST_MAX = 3;
    //  群聊信息列表
    private static ArrayList<GroupMessage> GroupMessageList = new ArrayList<>();


    /**
     * 好友信息列表（用户名和好友）
     */
    public static ConcurrentHashMap<String, List<String>> friendlist = new ConcurrentHashMap<>();

    /**
     * 群聊信息存储（群名和群成员）
     */
    public static ConcurrentHashMap<String, List<String>> grouplist = new ConcurrentHashMap<>();


    /**
     * 存储离线私人聊天
     */
    private static ConcurrentHashMap<String, List<Messages>> offlineMessageMap = new ConcurrentHashMap<>();


    /**
     * 存储群聊离线聊天
     */
    private static ConcurrentHashMap<String, List<GroupMessage>> offlineGroupMessage = new ConcurrentHashMap<>();

    /**
     * 存储离线添加好友通知
     */
    private static ConcurrentHashMap<String, List<AddFriend>> offlineAddFriend = new ConcurrentHashMap<>();

    /**
     * 存储离线邀请朋友进入群聊通知
     */
    private static ConcurrentHashMap<String, List<String>> offlineInviteFriend = new ConcurrentHashMap<>();

    /**
     * 存储离线更新群聊长度请求
     */
    private static ConcurrentHashMap<String, List<String>> offlineUpdateGroupLength= new ConcurrentHashMap<>();


    /**
     * 会话
     */
    private Session session;


    /**
     * 好友列表
     */
    //存储用户昵称
    public static List<Friends> friendsList = new ArrayList<>();

    /**
     * 群聊列表
     */
    //存储群聊名称
    public static List<Groups> groupsList = new ArrayList<>();


    /**
     * 定义并发HashMap存储好友WebSocket集合
     */
    public static ConcurrentHashMap<String, WebSocketController> webSocketSession = new ConcurrentHashMap<>();


    //打开连接
    @OnOpen
    public void onOpen(@PathParam(value = "nickname") String nickname, Session session) {
        System.out.println(grouplist.size());
        //确保输入昵称不为空
        if (StringUtils.hasText(nickname)) {
            // 设置session
            this.session = session;
            // Put添加当前类
            webSocketSession.put(nickname, this);
            // Add添加当前好友信息
            friendsList.add(Friends.builder().nickname(nickname).build());
            //进入会话，表示登陆成功

            // 更新好友列表
            updateFriendsList(nickname);

            // 通知更新群聊信息列表
            updateGroupInformationList(nickname);

            // 通知好友上线
            notifyFriendsOnline(nickname);

            // 好友上线通知
            send(nickname);

            // 发送相应不在群聊的好友
            updateNotInGroup(nickname);


            log.info("【WebSocket消息】有新的连接[{}], 连接总数:{}", nickname, webSocketSession.size());

            //离线消息发送（私聊消息）
            if (offlineMessageMap.containsKey(nickname)) {
                List<Messages> list = offlineMessageMap.get(nickname);
                Iterator<Messages> iterator = list.iterator();
                while (iterator.hasNext()){
                    Messages messages = iterator.next();
                    // 离线消息发送后删除消息
                    String result = sendOfflineMessageByUser(JSON.toJSONString(messages));
                    if("ok".equals(result)){
                        log.info("发送离线群聊消息成功！！！从离线消息队列中删除消息" + messages);
                        iterator.remove(); // 使用迭代器安全地移除元素
                    }
                }
            }

            // 离线群聊消息发送
            //检查群聊离线信息表中有没有自己的离线信息
            if (offlineGroupMessage.containsKey(nickname)) {
                List<GroupMessage> list = offlineGroupMessage.get(nickname);
                Iterator<GroupMessage> iterator = list.iterator();
                while (iterator.hasNext()) {
                    GroupMessage groupMessage = iterator.next();
                    // 离线消息发送成功后删除消息
                    String result = sendOfflineGroupMessage(nickname, JSON.toJSONString(groupMessage));
                    if ("ok".equals(result)) {
                        log.info("发送离线群聊消息成功！！！从离线消息队列中删除消息" + groupMessage);
                        iterator.remove(); // 使用迭代器安全地移除元素
                    }
                }
            }

            // 离线发送好友申请消息
            if (offlineAddFriend.containsKey(nickname)){
                List<AddFriend> list = offlineAddFriend.get(nickname);
                Iterator<AddFriend> iterator = list.iterator();
                while (iterator.hasNext()) {
                    AddFriend addFriend = iterator.next();
                    // 离线消息发送成功后删除消息
                    String result = sendAddFriends(nickname, JSON.toJSONString(addFriend));
                    if ("ok".equals(result)) {
                        log.info("发送离线群聊消息成功！！！从离线消息队列中删除消息" + addFriend);
                        iterator.remove(); // 使用迭代器安全地移除元素
                    }
                }
            }

            // 离线发送邀请好友进群消息
            if (offlineInviteFriend.containsKey(nickname)){
                List<String> list = offlineInviteFriend.get(nickname);
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()){
                    String message = iterator.next();
                    // 离线消息发送后删除消息
                    String result = inviteFriends(nickname, message);
                    if ("ok".equals(result)) {
                        log.info("发送离线邀请消息成功！！！从离线消息队列中删除消息" + message);
                        iterator.remove(); // 使用迭代器安全地移除元素
                    }

                }

            }

            // 离线更新群聊长度（主要用于创建群聊以及邀请好友进入群聊后的群聊人数的更新）
            if (offlineUpdateGroupLength.containsKey(nickname)){
                List<String> list = offlineUpdateGroupLength.get(nickname);
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()){
                    String message = iterator.next();
                    // 离线消息发送后删除消息
                    String result = updateLength(nickname, message);
                    if ("ok".equals(result)) {
                        log.info("发送离线邀请消息成功！！！从离线消息队列中删除消息" + message);
                        iterator.remove(); // 使用迭代器安全地移除元素
                    }

                }
            }


        }

    }




    /**
     * 更新好友列表
     * 主要将数据库中的好友更新到数据库
     */
    private synchronized void updateFriendsList(String nickname) {
        if(friendlist.containsKey(nickname) && friendlist.get(nickname) !=null){
            List<String> friendsList = friendlist.get(nickname);
            sendP2PMessage(nickname, JSON.toJSONString(Messages.builder().type("updateFriendsList").receiveNickname(nickname).messages(friendsList).build()));
        }
    }


    /**
     * 更新群聊列表
     * 主要用于将数据库中我所在的群聊发送至前端pinia状态管理
     */
    private synchronized void updateGroupInformationList(String user) {
        // 创建一个Map用于存储群名以及群成员
        ConcurrentHashMap<String, List<String>> Group = new ConcurrentHashMap<>();
        for (String groupName : grouplist.keySet()) {
            List<String> groupMembers = grouplist.get(groupName);
            if (groupMembers != null && groupMembers.contains(user)) {
                Group.put(groupName, groupMembers);
            }
        }
        // 类型，接收者，消息（群名和群成员）
        sendP2PMessage(user, JSON.toJSONString(Messages.builder().type("update-group").receiveNickname(user).messages(JSON.toJSONString(Group)).build()));
    }


    /**
     * 用于处理从客户端发送来的消息
     * @param nickname
     * @param message
     */
    @OnMessage(maxMessageSize = 10240000)
    public void onMessage(@PathParam(value = "nickname") String nickname, String message) {

        log.info("【WebSocket消息】 收到客户端[{}] 发送消息:{} 连接总数:{}", nickname, message, webSocketSession.size());
        // 验证消息内容
        if (StringUtils.hasLength(message)) {
            try {
                // 消息内容转消息对象
                Messages messages = JSON.parseObject(message, Messages.class);

                /*
                  私聊文本消息
                 */
                if ("messages".equals(messages.getType())) {
                    //敏感词过滤
                    String txt = messages.getMessages().toString();
                    String result = SensitiveWordHelper.replace(txt);
                    messages.setMessages(result);


                    String updatemessage = JSON.toJSONString(messages);
                    sendP2PChatMessage(messages.getReceiveNickname(), updatemessage);


                    //存储聊天记录
                    UserMessage userMessage = new UserMessage();
                    userMessage.setMessageType(messages.getType());
                    userMessage.setSender(messages.getSendNickname());
                    userMessage.setReceiver(messages.getReceiveNickname());
                    userMessage.setMessage(messages.getMessages().toString());
                    userMessage.setSendtime(new Date());
                    //存入消息列表
                    MessageList.add(userMessage);
                    //判断是否达到指定长度
                    if (MessageList.size() == LIST_SIZE) {
                        userMessageService.saveBatch(MessageList);
                        MessageList.clear();

                    }

                }
                /*
                  群聊文本消息
                 */
                else if ("group-message".equals(messages.getType())) {
                    // 解析为JSON对象
                    JSONObject jsonObject = JSON.parseObject(message);
                    // 获取群名
                    String groupname = jsonObject.getString("groupnickname");

                    // 敏感词过滤
                    String txt = jsonObject.getString("messages");
                    String result = SensitiveWordHelper.replace(txt);
                    jsonObject.put("messages", result);

                    // 发送群聊信息
                    sendgroupMessages(groupname, nickname, jsonObject.toString());

                    //存储群聊记录
                    GroupMessage groupMessage = new GroupMessage();
                    groupMessage.setType(jsonObject.getString("type"));
                    groupMessage.setGroupnickname(jsonObject.getString("groupnickname"));
                    groupMessage.setSendNickname(jsonObject.getString("sendNickname"));
                    groupMessage.setMessages(jsonObject.getString("messages"));
                    groupMessage.setSendTime(new Date());

                    //存放群聊消息
                    GroupMessageList.add(groupMessage);

                    //判断长度
                    if (GroupMessageList.size() == LIST_MAX) {
                        // 存入数据库
                        groupMessageService.saveBatch(GroupMessageList);
                        GroupMessageList.clear();
                    }

                }
                /*
                私人图片类型消息
                 */
                else if ("image".equals(messages.getType())) {
                    // 发送消息
                    sendP2PChatMessage(messages.getReceiveNickname(), message);

                    // 构造消息对象
                    UserMessage userMessageImage = new UserMessage();
                    userMessageImage.setMessageType(messages.getType());
                    userMessageImage.setSender(messages.getSendNickname());
                    userMessageImage.setReceiver(messages.getReceiveNickname());
                    userMessageImage.setMessage(messages.getMessages().toString());
                    userMessageImage.setSendtime(new Date());
                    //存入消息列表
                    MessageList.add(userMessageImage);
                    //判断是否达到指定长度
                    if (MessageList.size() == LIST_SIZE) {
                        // 存入数据库
                        userMessageService.saveBatch(MessageList);
                        MessageList.clear();

                    }
                }
                /*
                文件类型消息
                 */
                else if ("file".equals(messages.getType())) {
                    // 转发消息
                    sendP2PChatMessage(messages.getReceiveNickname(), message);

                    // 构造消息对象
                    UserMessage userMessageFile = new UserMessage();
                    userMessageFile.setMessageType(messages.getType());
                    userMessageFile.setSender(messages.getSendNickname());
                    userMessageFile.setReceiver(messages.getReceiveNickname());
                    userMessageFile.setMessage(messages.getMessages().toString());
                    userMessageFile.setSendtime(new Date());
                    //存入消息列表
                    MessageList.add(userMessageFile);
                    //判断是否达到指定长度
                    if (MessageList.size() == LIST_SIZE) {
                        // 存入数据库
                        userMessageService.saveBatch(MessageList);
                        MessageList.clear();
                    }
                }
                /*
                群聊图片消息
                 */
                else if ("group-image".equals(messages.getType())) {
                    // 解析为JSON对象
                    JSONObject jsonObject = JSON.parseObject(message);
                    // 获取群名
                    String groupname = jsonObject.getString("groupnickname");
                    // 发送消息
                    sendgroupMessages(groupname, nickname, message);

                    //存储群聊记录
                    GroupMessage groupMessage = new GroupMessage();
                    groupMessage.setType(jsonObject.getString("type"));
                    groupMessage.setGroupnickname(jsonObject.getString("groupnickname"));
                    groupMessage.setSendNickname(jsonObject.getString("sendNickname"));
                    groupMessage.setMessages(jsonObject.getString("messages"));
                    groupMessage.setSendTime(new Date());

                    //存放群聊消息
                    GroupMessageList.add(groupMessage);

                    //判断长度
                    if (GroupMessageList.size() == LIST_MAX) {
                        // 存入数据库
                        groupMessageService.saveBatch(GroupMessageList);
                        GroupMessageList.clear();
                    }

                }
                /*
                群聊文件消息
                 */
                else if ("group-file".equals(messages.getType())) {
                    // 解析为JSON对象
                    JSONObject jsonObject = JSON.parseObject(message);
                    // 获取群名
                    String groupname = jsonObject.getString("groupnickname");
                    // 转发消息
                    sendgroupMessages(groupname, nickname, message);

                    //存储群聊记录
                    GroupMessage groupMessage = new GroupMessage();
                    groupMessage.setType(jsonObject.getString("type"));
                    groupMessage.setGroupnickname(jsonObject.getString("groupnickname"));
                    groupMessage.setSendNickname(jsonObject.getString("sendNickname"));
                    groupMessage.setMessages(jsonObject.getString("messages"));
                    groupMessage.setSendTime(new Date());

                    //存放群聊消息
                    GroupMessageList.add(groupMessage);

                    //判断长度
                    if (GroupMessageList.size() == LIST_MAX) {
                        // 存入数据库
                        groupMessageService.saveBatch(GroupMessageList);
                        GroupMessageList.clear();
                    }
                }
                /*
                创建群聊
                 */
                else if ("createGroup".equals(messages.getType())) {
                    // 解析为Group对象
                    Group group = JSON.parseObject(message, Group.class);
                    // 获取群成员
                    List<String> list = group.getMessages();
                    // 群成员循环通知
                    for (String member : list){
                        // 好友在线通知
                        // 好友不在线可以从内存中读取
                        if(webSocketSession.containsKey(member)){
                            sendP2PMessage(member, message);
                        }
                    }
                    // 更新群成员的好友不在群聊列表
                    updateFriendNotInGroup(list);
//                    // 更新自己的好友不在列表
//                    updateNotInGroup(group.getCreator());
//                    // 更新群成员不在列表
//                    // 对群成员中的好友不在群聊进行更新
//                    updateFriendNotInGroup(group.getCreator());

                }
                /*
                添加好友，发送请求
                 */
                else if("addFriend".equals(messages.getType())){
                    // 解析为类对象
                    AddFriend addFriend = JSON.parseObject(message, AddFriend.class);
                    // 解析为JSON对象
                    JSONObject jsonObject = JSON.parseObject(message);
                    String username = jsonObject.getString("sendNickname");
                    String friendName = jsonObject.getString("messages");

                    // 先判断好友列表中是否存在该好友
                    // 列表没有该好友
                    if(repeat(username, friendName)){
                        // 先给好友发送一个添加好友请求
                        // 好友在线则发送
                        if(webSocketSession.containsKey(friendName)){
                            // 类型为添加好友请求
                            addFriend.setType("information");
                            // 发送者为被添加好友
                            addFriend.setSendNickname(friendName);
                            // 信息为添加好友者
                            addFriend.setMessages(username);
                            // 发送信息，内容为添加者
                            sendP2PMessage(friendName,JSON.toJSONString(addFriend));
                        }
                        // 好友不在线则存储到离线消息队列中去
                        else {
                            // 类型为添加好友请求
                            addFriend.setType("information");
                            // 发送者为被添加好友
                            addFriend.setSendNickname(friendName);
                            // 信息为添加好友者
                            addFriend.setMessages(username);

                            // 是否存在离线消息
                            // 存在
                            if(offlineAddFriend.containsKey(friendName)){
                                List<AddFriend> addFriends = offlineAddFriend.get(friendName);
                                addFriends.add(addFriend);
                                offlineAddFriend.put(friendName, addFriends);
                            }
                            // 不存在
                            else {
                                List<AddFriend> addFriends = new ArrayList<>();
                                addFriends.add(addFriend);
                                offlineAddFriend.put(friendName, addFriends);
                            }

                        }

                    }
                    // 列表中存在该好友
                    else {
                        // 重复添加
                        sendP2PMessage(username, JSON.toJSONString(ReturnMessage.builder().type("AddErr").receiveNickname(username).messagesSort("repeatAdd").build()));
                    }


                }

                /*
                好友同意添加
                 */
                else if ("addAgree".equals(messages.getType())) {
                    // 解析为AddFriend对象
                    AddFriend addFriend = JSON.parseObject(message, AddFriend.class);
                    // 解析为JSON对象
                    JSONObject jsonObject = JSON.parseObject(message);
                    // 添加好友者
                    String username = jsonObject.getString("messages");
                    // 同意者
                    String friendName = jsonObject.getString("sendNickname");

                    // 先进行重复检测
                    // 不可进行重复添加
                    if(repeat(friendName, username)){
                        // 添加至列表和数据库中
                        addFriendsController.addFriends(username, friendName);

                        // 更新好友不在群聊列表
                        // 更新添加好友的
                        updateNotInGroup(username);
                        // 更新被添加者
                        updateNotInGroup(friendName);

                        // 发送给同意者
                        // 自己点击同意一定在线
                        if (webSocketSession.containsKey(username)){
                            sendP2PMessage(addFriend.getSendNickname(),message);
                        }
                        // 好友不在线
                        else{
                            addFriend.setStatus(false);
                            sendP2PMessage(addFriend.getSendNickname(), message);
                            // 将成功标志进行赋值
                        }

                        // 发送给添加好友的
                        // 添加好友者在线
                        // 不在线登陆时加载无需发送
                        if(webSocketSession.containsKey(username)){
                            addFriend.setSendNickname(jsonObject.getString("messages"));
                            addFriend.setMessages(jsonObject.getString("sendNickname"));
                            sendP2PMessage(username, JSON.toJSONString(addFriend));
                        }
                    }

                }
                /*
                邀请入群
                 */
                else if ("InviteFriends".equals(messages.getType())) {
                    // 解析为类对象
                    InviteFriend inviteFriend = JSON.parseObject(message, InviteFriend.class);
                    List<String> list = inviteFriend.getMessages();
                    for (String friend: list){
                        // 组装信息
                        String InformMessage = JSON.toJSONString(InviteFriend.builder().type("InviteFriends").Inviter(inviteFriend.getInviter()).groupName(inviteFriend.getGroupName()).build());
                        // 判断好友是否在线
                        // 在线
                        if(webSocketSession.containsKey(friend)){
                            // 在线发送给好友邀请信息
                            sendP2PMessage(friend, InformMessage);
                        }
                        // 不在线则存储到离线信息
                        else {
                            if(offlineInviteFriend.containsKey(friend)){
                                List<String> list1 = offlineInviteFriend.get(friend);
                                list1.add(InformMessage);
                                offlineInviteFriend.put(friend, list1);
                            }
                            else {
                                List<String> list1 = new ArrayList<>();
                                list1.add(InformMessage);
                                offlineInviteFriend.put(friend,list1);
                            }
                        }
                    }

                }

                /*
                同意入群
                 */
                else if("AgreeToGroup".equals(messages.getType())){
                    // 解析为JSON对象
                    JSONObject jsonObject = JSONObject.parseObject(message);
                    // 群名
                    String GroupName = jsonObject.getString("groupName");
                    // 同意者
                    String name = jsonObject.getString("receiver");


                    if(!"Group not found".equals(groupLength(GroupName))){
                        // 存入群列表中（内存中）
                        List<String> members = grouplist.get(GroupName);
                        members.add(name);
                        grouplist.put(GroupName, members);

                        // 创建对象
                        AddGroup addGroup = new AddGroup();
                        addGroup.setType("AgreeToGroup");
                        addGroup.setGroupLength(groupLength(GroupName));
                        addGroup.setGroupName(GroupName);
                        addGroup.setMember(members);

                        // 通知好友更新群聊长度
                        updateGroupmembers(name, GroupName, members);

//                        // 更新好友不在群聊
//                        updateNotInGroup(name);
                        // 更新其他用户不在群聊列表
                        updateFriendNotInGroup(members);

                        // 存入数据库
                        createGroupController.updateGroupToDatabase(GroupName, members);
                        // 通知同意加入群聊者更新群聊信息
                        sendP2PMessage(jsonObject.getString("receiver"), JSON.toJSONString(addGroup));
                    }

                }
                else {
                    log.info("没有这种聊天类型");
                }


            } catch (Exception e) {
                log.error("WebSocket消息异常:", e);
            }
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "nickname") String nickname) {

        // 如果列表里边还有信息
        // 存储至数据库
        if (MessageList.size() < LIST_SIZE) {
            userMessageService.saveBatch(MessageList);
            MessageList.clear();
        }

        // 群聊信息列表内还有消息
        // 存储至数据库
        if (GroupMessageList.size() < LIST_MAX) {
            groupMessageService.saveBatch(GroupMessageList);
            GroupMessageList.clear();
        }

        // 通知好友下线
        notifyFriendsOffline(nickname);

        friendsList.remove(friendsList.stream().filter((friends -> friends.getNickname().equals(nickname))).findAny().orElse(null));
        webSocketSession.remove(nickname);

        log.info("【WebSocket消息】客户端[{}]连接断开, 剩余连接总数:{}", nickname, webSocketSession.size());
    }



    /**
     * 发送消息，用于更新好友在线与离线
     */
    // 通知好友我已经上线
    private static synchronized void notifyFriendsOnline(String nickname) {
        List<String> friendslist = friendlist.get(nickname);
        if(friendslist != null){
            for (String friend : friendslist) {

                // 如果好友在线
                // 发送消息
                if (webSocketSession.containsKey(friend)) {
                    // 定义消息
                    String message = JSON.toJSONString(Messages.builder().type("friendOnline").receiveNickname(friend).messages(nickname).build());
                    // 发送给在线好友
                    sendP2PMessage(friend, message);

                }
            }
        }
    }
    // 好友上线更新
    // 给自己发送好友在线
    private static synchronized void send(String username){
        List<String> list = friendlist.get(username);
        if(list != null){
            for(String friend : list){
                if(webSocketSession.containsKey(friend)){
                    String messageOffline = JSON.toJSONString(Messages.builder().type("friendOnline").receiveNickname(username).messages(friend).build());
                    sendP2PMessage(username, messageOffline);
                }
            }
        }

    }








    /**
     * 发送离线消息（离线消息）
     */
    public static synchronized String sendOfflineMessageByUser(String message) {
        Messages messages = JSON.parseObject(message, Messages.class);

        try {
            webSocketSession.get(messages.getReceiveNickname()).session.getBasicRemote().sendText(message);
            return "sucess";
        } catch (IOException e) {
            log.error("离线发送发送异常:", e);
        }
        return "ok";
    }

    /**
     * 发送离线群聊消息（离线消息）
     */
    public static synchronized String sendOfflineGroupMessage(String key, String groupMessage) {
        try {
            webSocketSession.get(key).session.getBasicRemote().sendText(groupMessage);
            log.info("【离线消息】群发消息, Receiver: {}, Message: {}", key, groupMessage);
            return "ok";
        } catch (IOException E) {
            log.info("群聊信息离线发送出现异常：", E);
        }
        return "finish";
    }

    /**
     * 发送离线好友申请消息
     */
    public static synchronized String sendAddFriends(String name, String message){
        log.info("【WebSocket消息】点对点发送消息, nickname={} , message={}", name, message);
        try {
            webSocketSession.get(name).session.getBasicRemote().sendText(message);
            return "ok";
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
            return "error";
        }
    }

    /**
     * 发送离线邀请朋友进群
     */
    public static synchronized String inviteFriends(String name, String message){
        log.info("【WebSocket消息】点对点发送消息, nickname={} , message={}", name, message);
        try {
            webSocketSession.get(name).session.getBasicRemote().sendText(message);
            return "ok";
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
            return "error";
        }
    }
    /**
     * 发送离线更新群聊长度
     */
    public static synchronized String updateLength(String name, String message){
        log.info("【WebSocket消息】点对点发送消息, nickname={} , message={}", name, message);
        try {
            webSocketSession.get(name).session.getBasicRemote().sendText(message);
            return "ok";
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
            return "error";
        }
    }






    /**
     * 点对点发送更新消息（用于更新）
     */
    public static synchronized void sendP2PMessage(String nickname, String message) {
        log.info("【WebSocket消息】点对点发送消息, nickname={} , message={}", nickname, message);
        try {
            webSocketSession.get(nickname).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("点对点发送异常:", e);
        }
    }





    /**
     * 群聊发送消息
     */
    public static synchronized void sendgroupMessages(String groupname, String sender, String message) {

        //先转化成群聊信息对象
        GroupMessage groupMessage = JSON.parseObject(message, GroupMessage.class);
        //通过群名找到对应的群成员
        List<String> groupMembers = grouplist.get(groupname);
        //找到相应群组，对应群组存在
        if (groupMembers != null) {
            groupMembers.forEach((key) -> {
                if (!key.equals(sender)) { // 排除发送者自己，发送者自己一定在线
                    WebSocketController webSocketController = webSocketSession.get(key);
                    //群成员在线
                    if (webSocketController != null) {
                        try {
                            // 发送消息给群成员
                            webSocketSession.get(key).session.getBasicRemote().sendText(message);
                            log.info("【群聊消息】群发消息, Group: {}, Sender: {}, Receiver: {}, Message: {}", groupname, sender, key, message);
                        } catch (IOException e) {
                            // 发送消息失败时记录错误日志
                            e.printStackTrace();
                        }
                    }
                    //某一个群成员不在线
                    //存储离线消息
                    else {
                        log.info("成员不在线的有：：" + key);
                        //如果离线消息已存在
                        if (offlineGroupMessage.containsKey(key)) {
                            List<GroupMessage> groupMessages = offlineGroupMessage.get(key);
                            groupMessages.add(groupMessage);
                            offlineGroupMessage.put(key, groupMessages);
                            log.info("离线消息存储成功！！！！");
                        }
                        //如果没有离线消息
                        else {
                            List<GroupMessage> list = new ArrayList<>();
                            list.add(groupMessage);
                            offlineGroupMessage.put(key, list);
                            log.info("离线消息存储成功！！！！");
                        }
                    }

                }
            });
        } else {
            // 如果群组不存在，则记录错误日志
            System.err.println("【群聊消息】群组 " + groupname + " 不存在.");
        }
    }


    /**
     * 点对点发送消息（聊天消息）
     */
    //原来是void
    public static synchronized String sendP2PChatMessage(String nickname, String message) {
        Messages messages = JSON.parseObject(message, Messages.class);
        log.info("【WebSocket消息】点对点发送消息, nickname={} , message={}", nickname, message);
        //获取接收者在线状况
        WebSocketController webSocketController = webSocketSession.get(messages.getReceiveNickname());
        //发送离线信息
        if (webSocketController == null || !webSocketController.session.isOpen()) {
            if (offlineMessageMap.containsKey(messages.getReceiveNickname())) {
                List<Messages> list = offlineMessageMap.get(messages.getReceiveNickname());
                list.add(messages);
                offlineMessageMap.put(messages.getReceiveNickname(), list);
            } else {
                List<Messages> list1 = new ArrayList<>();
                list1.add(messages);
                offlineMessageMap.put(messages.getReceiveNickname(), list1);
            }
            return "offline";

        } else {
            try {
                webSocketSession.get(nickname).session.getBasicRemote().sendText(message);
                return "ok";
            } catch (IOException e) {
                log.error("点对点发送异常:", e);
                return "error";
            }
        }


    }

    /***
     * 好友离线通知
     * @param nickname
     * 发送离线好友昵称
     */
    private static synchronized void notifyFriendsOffline(String nickname) {

        //获取该用户的好友列表
        List<String> friends = friendlist.get(nickname);
        if(friends != null){
            // 循环检查是否在线
            for (String friend : friends) {
                String messageOffline = JSON.toJSONString(Messages.builder().type("friendOffline").receiveNickname(friend).messages(nickname).build());
                // 用户在线
                if (webSocketSession.containsKey(friend)) {
                    log.info(messageOffline);
                    // 通知好友该用户已离线
                    sendP2PMessage(friend, messageOffline);
                }

            }
        }

    }








    // 判断好友列表中是否存在要添加好友
    private static synchronized boolean repeat(String username, String friend) {
        // 如果好友列表不包括用户，返回true
        if (!friendlist.containsKey(username)) {
            return true;
        }
        List<String> list = friendlist.get(username);
        return !(list != null && list.contains(friend));
    }



    // 获取我的好友列表
    private static synchronized List<String> AcFriends(String nickname){
        if(friendlist.containsKey(nickname) && friendlist.get(nickname) !=null ){
            return friendlist.get(nickname);
        }
        else {
            return new ArrayList<>() ;
        }

    }

    // 将未在某个群里的好友加载到前端页面
    // 用于邀请好友
    private static synchronized void updateNotInGroup(String nickname){
        // 为空返回
        if (nickname == null || nickname.isEmpty()) {
            return;
        }

        // 储存好友列表
        ConcurrentHashMap<String, List<String>> group = new ConcurrentHashMap<>();
        for (String groupName : grouplist.keySet()) {
            // 根据每个键值进行查找群成员
            List<String> groupMembers = grouplist.get(groupName);
            // 群聊不为空且群聊包括我
            if (groupMembers != null && groupMembers.contains(nickname)) {
                // 获取我的好友信息
                List<String> list = AcFriends(nickname);
                // 确保好友列表不为空
                if(list != null){
                    List<String> notIn = new ArrayList<>();
                    // 循环
                    for (String friendName: list){
                        // 如果好友在群聊
                        if(!groupMembers.contains(friendName)){
                            notIn.add(friendName);
                        }

                    }
                    group.put(groupName, notIn);
                }
            }
        }

        // 输出结果
        log.info("最终的结果是："+JSON.toJSONString(group));

        // 前端发送不在群聊好友列表
        sendP2PMessage(nickname, JSON.toJSONString(NotInGroup.builder().type("update-information").messages(JSON.toJSONString(group)).build()));

    }

    // 返回一个群聊的长度
    private static synchronized String groupLength(String groupName){
        if (grouplist.containsKey(groupName) && grouplist.get(groupName)!=null){
            Integer length = grouplist.get(groupName).size();
            String groupLength = length.toString();
            return groupLength;
        }
        // 为空或者群聊不存在
        return "Group not found";
    }


    // 新人加入群聊，更新其他群友中的群聊人数
    private static synchronized void updateGroupmembers(String friend, String groupName, List<String> memberlist){
        for (String nickname: memberlist){
            if(!Objects.equals(nickname, friend)){
                UpdateGroupNumber updateGroupNumber = new UpdateGroupNumber();
                updateGroupNumber.setType("updateGroupNumber");
                updateGroupNumber.setReceiver(nickname);
                updateGroupNumber.setGroupName(groupName);
                Integer length = memberlist.size();
                updateGroupNumber.setGroupLength(length.toString());

                // 发送的消息
                String message = JSON.toJSONString(updateGroupNumber);
                // 发送消息
                // 首先判断是否在线
                if(webSocketSession.containsKey(nickname)){
                    // 在线发送消息
                    sendP2PMessage(nickname, message);
                }
                // 如果不在线
                else {
                    if(offlineUpdateGroupLength.containsKey(nickname)&&offlineUpdateGroupLength.get(nickname)!=null){
                        List<String> list = offlineUpdateGroupLength.get(nickname);
                        list.add(message);
                        offlineUpdateGroupLength.put(nickname, list);
                    }else {
                        List<String> list = new ArrayList<>();
                        list.add(message);
                        offlineUpdateGroupLength.put(nickname, list);
                    }
                }
            }
        }
    }

    // 创建群聊，存储相应信息
    public static void createGroup(String groupNickname, List<String> members) {
        //存储群名和群成员
        grouplist.put(groupNickname, members);
        System.out.println("群聊 '" + groupNickname + "' 创建成功！");
    }

    // 存储对应用户好友信息
    public static void addFriends(String username, List<String> friends) {
        friendlist.put(username, friends);
        log.info("好友存储完毕！！");
    }

    /*
    更新好友的不在群聊
     */
    public synchronized void updateFriendNotInGroup(List<String> members){
        if(members == null || members.isEmpty()){
            return;
        }
        // 对群成员发送更新好友不在列表中的信息
        for (String name: members){
            // 判断是否在线
            if(webSocketSession.containsKey(name)){
                updateNotInGroup(name);
            }

        }
    }


    // 获取群聊成员列表
    public static void getGroupMembers(String groupNickname) {
        List<String> groupMembers = grouplist.get(groupNickname);
        if (groupMembers != null) {
            System.out.println("群聊 '" + groupNickname + "' 成员列表：");
            for (String member : groupMembers) {
                System.out.println(member);
            }
        } else {
            System.out.println("群聊 '" + groupNickname + "' 不存在！");
        }
    }
}