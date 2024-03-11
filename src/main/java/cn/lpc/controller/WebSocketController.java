package cn.lpc.controller;

import cn.lpc.entity.Friends;
import cn.lpc.entity.Groups;
import cn.lpc.entity.Messages;
import com.alibaba.fastjson.JSON;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{nickname}")
public class WebSocketController {


    /**
     *会话
     */
    private Session session;


    /**
     * 好友列表
     */
    //存储好友信息
    public static List<Friends> friendsList = new ArrayList<>();

    /**
     * 群聊列表
     */
    //存储群聊信息
    public static List<Groups> groupsList = new ArrayList<>();

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketController>> groupChats = new ConcurrentHashMap<>();

    //创建群组
    private static void createGroup(String groupnickname){
        //群聊成员
        ConcurrentHashMap<String,WebSocketController> groupMembers = new ConcurrentHashMap<>();
        //添加成员
        groupChats.put(groupnickname , groupMembers);

        log.info("群聊"+groupnickname+"创建成功！！！！");

    }

    //加入群聊
    public static void joinGroup(String groupNickname, String username, WebSocketController controller) {
        ConcurrentHashMap<String, WebSocketController> groupMembers = groupChats.get(groupNickname);
        if (groupMembers != null) {
            groupMembers.put(username, controller);
            System.out.println(username + " 加入群聊 '" + groupNickname + "'");
        } else {
            System.out.println("群聊 '" + groupNickname + "' 不存在！");
        }
    }

    // 获取群聊成员列表
    public static void getGroupMembers(String groupNickname) {
        ConcurrentHashMap<String, WebSocketController> groupMembers = groupChats.get(groupNickname);
        if (groupMembers != null) {
            System.out.println("群聊 '" + groupNickname + "' 成员列表：");
            for (String username : groupMembers.keySet()) {
                System.out.println("- " + username);
            }
        } else {
            System.out.println("群聊 '" + groupNickname + "' 不存在！");
        }
    }

    public static void joinGroupChat(String nickname, WebSocketController session, String groupName) {
        // 获取或创建群组
        groupChats.computeIfAbsent(groupName, k -> new ConcurrentHashMap<>());
        // 将用户加入群组
        groupChats.get(groupName).put(nickname, session);
//        sendP2PMessage(nickname, JSON.toJSONString(Messages.builder().type("createGroup").receiveNickname(nickname).messages(groupName).build()));
//        webSocketSession.forEach((key, val) -> {
//            // 初始化存储属于自己的好友列表 排除自己
//            List<Friends> friends = new ArrayList<>();
//            // 迭代所有好友列表
//            friendsList.forEach((friend) -> {
//                // 在 所有好友信息列表 中验证非自己
//                if (!friend.getNickname().equals(key)) {
//                    // 追加非自己的好友信息
//                    friends.add(friend);
//                }
//            });
//            // 发送消息
//            sendP2PMessage(key, JSON.toJSONString(Messages.builder().type("createGroup").receiveNickname(key).messages(friends).groupnickname(groupName).build()));
//        });
        sendP2PMessage(nickname , JSON.toJSONString(Messages.builder().type("createGroup").messages(friendsList).groupnickname(groupName).build()));
    }
    //***************************************************


    /**
     * 定义并发HashMap存储好友WebSocket集合
     */
    public static ConcurrentHashMap<String, WebSocketController> webSocketSession = new ConcurrentHashMap<>();


    //打开连接
    @OnOpen
    public void onOpen(@PathParam(value = "nickname") String nickname, Session session) {
        //确保输入昵称不为空
        if(StringUtils.hasText(nickname)) {
            // 设置session
            this.session = session;
            // Put添加当前类
            webSocketSession.put(nickname, this);
            // Add添加当前好友信息
            friendsList.add(Friends.builder().nickname(nickname).build());
            //进入会话，表示登陆成功

            //*************************
            groupsList.add(Groups.builder().groupnickname("打雷").build());
            //*************************
            //测试群名
//            log.info(groupsList.get(0).getGroupnickname());

            // 通知更新好友信息列表
            updateFriendInformationList();

            //***************************
            // 通知更新群聊信息列表
            updateGroupInformationList();
            //***************************

            //加入群聊
            joinGroupChat(nickname , this ,groupsList.get(0).getGroupnickname());

            //遍历外层，输出群名
            String groupName = "打雷";  // 指定群组名
            ConcurrentHashMap<String, WebSocketController> groupMembers = groupChats.get(groupName);

            if (groupMembers != null) {
                Set<String> memberNicknames = groupMembers.keySet();
                // 遍历指定群组的所有成员昵称
                for (String memberNickname : memberNicknames) {
                    log.info("群组名叫: {} ,成员有: {}",groupName,memberNickname);
                }
            } else {
                log.info("群组不存在：{}",groupName);
            }
            //



            log.info("【WebSocket消息】有新的连接[{}], 连接总数:{}", nickname, webSocketSession.size());
        }

    }


    /**
     * 通知更新好友信息列表
     */
    private synchronized void updateFriendInformationList() {
        webSocketSession.forEach((key, val) -> {
            // 初始化存储属于自己的好友列表 排除自己
            List<Friends> friends = new ArrayList<>();
            // 迭代所有好友列表
            friendsList.forEach((friend) -> {
                // 在 所有好友信息列表 中验证非自己
                if (!friend.getNickname().equals(key)) {
                    // 追加非自己的好友信息
                    friends.add(friend);
                }
            });
            // 发送消息
            sendP2PMessage(key, JSON.toJSONString(Messages.builder().type("updateFriendsList").receiveNickname(key).messages(friends).build()));
        });
    }

    //**************************************************

    /**
     *通知更新群聊信息列表
     */
    private synchronized void updateGroupInformationList(){
        webSocketSession.forEach((key, val) -> {
            // 初始化存储属于自己的好友列表 排除自己
            List<Friends> friends = new ArrayList<>();
            // 迭代所有好友列表
            friendsList.forEach((friend) -> {
                // 在 所有好友信息列表 中验证非自己
                if (!friend.getNickname().equals(key)) {
                    // 追加非自己的好友信息
                    friends.add(friend);
                }
            });
            // 发送消息
            sendP2PMessage(key, JSON.toJSONString(Messages.builder().type("update-group").receiveNickname(key).messages(friends).groupnickname("打雷").build()));
        });
//        groupChats.forEach((key , val) -> {
//            List<Groups> groups = new ArrayList<>();
//            groupsList.forEach((groups1) -> {
//                groups.add(groups1);
//            });
//            val.forEach((key1 , val1) -> {
//                sendP2PMessage(key1,JSON.toJSONString(Messages.builder().type("update-group").receiveNickname(key1).messages(groups).build()));
//            });
//        });
    }
    //**************************************************


    //处理客户端发送的消息：即前端发送来的消息
    @OnMessage(maxMessageSize = 10240000)
    public void onMessage(@PathParam(value = "nickname") String nickname, String message) {

        log.info("【WebSocket消息】 收到客户端[{}] 发送消息:{} 连接总数:{}", nickname, message, webSocketSession.size());
        // 验证消息内容
        if (StringUtils.hasLength(message)) {
            try {
                // 消息内容转消息对象
                Messages messages = JSON.parseObject(message, Messages.class);

                //聊天记录存入数据库
//                messagesController.Insert(messages.getSendNickname() , messages.getReceiveNickname() , messages.getMessages().toString() , messages.getType());

                //
                if("messages".equals(messages.getType())){
                    // 私聊发送消息
                    sendP2PMessage(messages.getReceiveNickname(), message);
                } else if ("group-message".equals(messages.getType())) {
                    sendGroupMessage(groupsList.get(0).getGroupnickname(), nickname , message);
                } else if("image".equals(messages.getType())){
                    sendP2PMessage(messages.getReceiveNickname(), message);
                } else if ("file".equals(messages.getType())) {
                    sendP2PMessage(messages.getReceiveNickname() , message);
                } else {
                    log.info("没有这种聊天类型");
                }

            } catch (Exception e) {
                log.error("WebSocket消息异常:", e);
            }
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "nickname") String nickname) {
        friendsList.remove(friendsList.stream().filter((friends -> friends.getNickname().equals(nickname))).findAny().orElse(null));
        groupsList.remove(groupsList.stream().filter((groups -> groups.getGroupnickname().equals("打雷"))).findAny().orElse(null));
        webSocketSession.remove(nickname);
        // 通知更新好友信息列表
        updateFriendInformationList();

        updateGroupInformationList();
        log.info("【WebSocket消息】客户端[{}]连接断开, 剩余连接总数:{}", nickname, webSocketSession.size());
    }

    /**
     * 点对点发送
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
     * 群聊
     */

    // 发送群组消息
//    public static void sendGroupMessage(String groupName, String senderNickname, String message) {
//        ConcurrentHashMap<String, WebSocketController> groupMembers = groupChats.get(groupName);
//        if (groupMembers != null) {
//            groupMembers.forEach((key, val) -> {
//                try {
//                    webSocketSession.get(key).session.getBasicRemote().sendText(message);
//                    log.info("【群聊消息】群发消息,groupname = {} sender = {} , message = {}", groupName, senderNickname, message);
//                } catch (IOException e) {
//                    log.error("发送消息给用户时发生错误：{}", key, e);
//                }
//            });
//        } else {
//            log.error("Group {} does not exist.", groupName);
//        }
//    }
    public static void sendGroupMessage(String groupName, String senderNickname, String message) {
        ConcurrentHashMap<String, WebSocketController> groupMembers = groupChats.get(groupName);
        if (groupMembers != null) {
            groupMembers.forEach((key, val) -> {
                if (!key.equals(senderNickname)) { // 排除发送者自己
                    try {
                        val.session.getBasicRemote().sendText(message);
                        log.info("【群聊消息】群发消息, Group: {}, Sender: {}, Receiver: {}, Message: {}", groupName, senderNickname, key, message);
                    } catch (IOException e) {
                        log.error("【群聊消息】发送消息给用户时发生错误：{}", key, e);
                    }
                }
            });
        } else {
            log.error("【群聊消息】群组 {} 不存在.", groupName);
        }
    }



}