package cn.lpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    /**
     * 状态 （status）
     */
    private int status;
    /**
     * 返回信息（message）
     */
    private String message;
    /**
     * 返回数据（data）
     */
    private Object data;
}