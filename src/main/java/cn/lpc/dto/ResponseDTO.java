//package cn.lpc.dto;
//
//import cn.lpc.bean.CodeMsg;
//
//
///**
// * 返回类数据传输对象  作用于service和controller
// * @param <T>
// */
//public class ResponseDTO<T> {
//    private Integer code;
//
//    private String msg;
//
//    private T data;
//
//    public Integer getCode() {
//        return code;
//    }
//
//    public void setCode(Integer code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public T getData() {
//        return data;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    private ResponseDTO(Integer code, String msg) {
//        this.code = code;
//        this.msg = msg;
//    }
//
//    private ResponseDTO(Integer code, T data) {
//        this.code = code;
//        this.data = data;
//    }
//
//    private ResponseDTO(Integer code, String msg, T data) {
//        this.code = code;
//        this.msg = msg;
//        this.data = data;
//    }
//
//    public static <T> ResponseDTO<T> success(T data) {
//        return new ResponseDTO<>(CodeMsg.SUCCESS.getCode(), data);
//    }
//
//    public static <T> ResponseDTO<T> success(T data, String msg) {
//        return new ResponseDTO<>(CodeMsg.SUCCESS.getCode(), msg, data);
//    }
//
//    public static <T> ResponseDTO<T> error(String codeMsg) {
//        return new ResponseDTO<>();
//    }
//
//}
