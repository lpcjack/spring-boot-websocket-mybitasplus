package cn.lpc.bean;


/**
 * 错误码统一处理类，所有的错误码统一定义在这里
 */
public class CodeMsg {

    private Integer code;//错误码

    private String msg;//错误信息

    /**
     * 构造函数私有化即单例模式
     * 该类负责创建自己的对象，同时确保只有单个对象被创建。这个类提供了一种访问其唯一的对象的方式，可以直接访问，不需要实例化该类的对象。
     * @param code
     * @param msg
     */
    private CodeMsg(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg() {

    }

    public Integer getCode() {
        return code;
    }



    public void setCode(Integer code) {
        this.code = code;
    }



    public String getMsg() {
        return msg;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    //通用错误码定义
    //处理成功消息码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    //通用数据错误码
    public static CodeMsg DATA_ERROR = new CodeMsg(-1, "非法数据！");
    public static CodeMsg VALIDATE_ENTITY_ERROR = new CodeMsg(-2, "");
    public static CodeMsg CAPTCHA_EMPTY = new CodeMsg(-3, "验证码不能为空!");
    public static CodeMsg NO_PERMISSION = new CodeMsg(-4, "您没有当前操作的权限哦！");
    public static CodeMsg CAPTCHA_ERROR = new CodeMsg(-5, "验证码错误！");
    public static CodeMsg USER_SESSION_EXPIRED = new CodeMsg(-6, "还未登录或会话失效，请重新登录！");
    public static CodeMsg UPLOAD_PHOTO_SUFFIX_ERROR = new CodeMsg(-7, "图片格式不正确！");
    public static CodeMsg PHOTO_SURPASS_MAX_SIZE = new CodeMsg(-8, "上传的图片不能超过1MB！");
    public static CodeMsg PHOTO_FORMAT_NOT_CORRECT = new CodeMsg(-9, "上传的图片格式不正确！");
    public static CodeMsg SAVE_FILE_EXCEPTION = new CodeMsg(-10, "保存文件异常！");
    public static CodeMsg FILE_EXPORT_ERROR = new CodeMsg(-11, "文件导出失败！");
    public static CodeMsg SYSTEM_ERROR = new CodeMsg(-12, "系统出现了错误，请联系管理员！");
    public static CodeMsg NO_AUTHORITY = new CodeMsg(-13, "不好意思，您没有权限操作哦！");
    public static CodeMsg CAPTCHA_EXPIRED = new CodeMsg(-14, "验证码已过期，请刷新验证码！");
    public static CodeMsg COMMON_ERROR = new CodeMsg(-15, "");
    public static CodeMsg PHOTO_EMPTY = new CodeMsg(-16, "上传的图片不能为空！");
    public static CodeMsg FILE_EMPTY = new CodeMsg(-16, "上传的文件不能为空！");
    public static CodeMsg FILE_SURPASS_MAX_SIZE = new CodeMsg(-8, "上传的文件不能超过300MB！");

    //用户管理类错误码
    public static CodeMsg USER_ADD_ERROR = new CodeMsg(-1000, "用户信息添加失败，请联系管理员！");
    public static CodeMsg USER_NOT_EXIST  = new CodeMsg(-1001, "该用户不存在！");
    public static CodeMsg USER_EDIT_ERROR = new CodeMsg(-1002, "用户信息编辑失败，请联系管理员！");
    public static CodeMsg USER_DELETE_ERROR = new CodeMsg(-1003, "用户信息删除失败，请联系管理员！");
    public static CodeMsg USERNAME_EXIST = new CodeMsg(-1004, "用户昵称重复，请换一个！");
    public static CodeMsg USERNAME_EMPTY = new CodeMsg(-1005, "用户昵称不能为空！");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(-1006, "用户密码不能为空！");
    public static CodeMsg USERNAME_PASSWORD_ERROR = new CodeMsg(-1007, "用户昵称或密码错误！");
    public static CodeMsg REPASSWORD_EMPTY = new CodeMsg(-1008, "确认密码不能为空！");
    public static CodeMsg REPASSWORD_ERROR = new CodeMsg(-1009, "确认密码不一致！");
    public static CodeMsg USER_REGISTER_ERROR = new CodeMsg(-1010, "注册用户失败，请联系管理员！");
    public static CodeMsg USER_NOT_IS_ADMIN = new CodeMsg(-1011, "只有管理员角色才能登录后台系统！");


    //好友管理错误码
    public static CodeMsg FRIEND_APPLY_EXIST = new CodeMsg(-4000, "申请失败，已经是好友或已发送申清！");
    public static CodeMsg FRIEND_APPLY_ERROR = new CodeMsg(-4001, "申请添加好友失败，请联系管理员！");
    public static CodeMsg AGREE_FRIEND_ERROR = new CodeMsg(-4002, "同意添加好友失败，请联系管理员！");
    public static CodeMsg REFUSE_FRIEND_ERROR = new CodeMsg(-4003, "拒绝好友申请失败，请联系管理员！");
    public static CodeMsg DELETE_FRIEND_ERROR = new CodeMsg(-4004, "删除好友失败，请联系管理员！");
    public static CodeMsg NO_PERMIT_ADD = new CodeMsg(-4005, "不能加自己为好友哟！");

    //消息管理错误码
    public static CodeMsg MESSAGE_SEND_ERROR = new CodeMsg(-5000, "消息发送失败，请联系管理员！");
    public static CodeMsg MESSAGE_NOT_EXIST = new CodeMsg(-5001, "此消息已不存在！");
    public static CodeMsg FILE_MESSAGE_DOWNLOAD_ERROR = new CodeMsg(-5002, "文件消息下载失败！");

    //会话管理错误码
    public static CodeMsg CHAT_UPDATE_ERROR = new CodeMsg(-6000, "会话信息更新失败，请联系管理员！");
    public static CodeMsg CHAT_ADD_ERROR = new CodeMsg(-6001, "会话信息创建失败，请联系管理员！");
    public static CodeMsg CHAT_NOT_EXIST = new CodeMsg(-6002, "会话信息不存在！");
    public static CodeMsg CHAT_DELETE_ERROR = new CodeMsg(-6003, "会话信息删除失败，请联系管理员！");

    //群聊管理错误码
    public static CodeMsg GROUP_ITEM_EMPTY = new CodeMsg(-7000, "未选择邀请好友，群聊创建失败！");
    public static CodeMsg GROUP_ADD_ERROR = new CodeMsg(-7001, "群聊创建失败，请联系管理员！");
    public static CodeMsg GROUP_NOT_JOIN = new CodeMsg(-7002, "您未加入群聊！");
    public static CodeMsg GROUP_START_CHAT_ERROR = new CodeMsg(-7003, "发起群聊会话失败，请联系管理员！");
    public static CodeMsg GROUP_INVITE_ERROR = new CodeMsg(-7004, "邀请加入群聊失败，请联系管理员！");
    public static CodeMsg GROUP_ITEM_EXIST = new CodeMsg(-7005, "当前用户已加入群聊！");
    public static CodeMsg GROUP_NOT_EXIST = new CodeMsg(-7006, "群聊已不存在！");
    public static CodeMsg GROUP_EXIT_ERROR = new CodeMsg(-7007, "退出群聊失败，请联系管理员！");
    public static CodeMsg GROUP_EDIT_ERROR = new CodeMsg(-7008, "群聊信息更新失败，请联系管理员！");
}
