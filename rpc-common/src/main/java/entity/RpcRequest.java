package entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 调用方向提供方发送的请求对象建造者模式注解
 *               Builder是建造者模式注解
 * @Author: cosine
 * @Date: 2021/10/2 2:33 下午
 * @Version: 1.0.0
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 待调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
}
