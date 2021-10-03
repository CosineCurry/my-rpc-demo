package com.cosine.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 传输的对象
 * @Author: cosine
 * @Date: 2021/9/30 8:16 下午
 * @Version: 1.0.0
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;

    private String message;

}
