package com.cosine.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 包类型的枚举类，类似TCP的包，接受包和响应包
 * @author cosine
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
