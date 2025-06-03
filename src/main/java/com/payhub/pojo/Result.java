package com.payhub.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 0 success; 1 fail
    private String msg; // "成功"
    private T data; // 响应数据
    private Integer count; // 数据中的元素个数

    // 修改 success 方法，增加 count 参数
    public static <E> Result<E> success(E data, Integer count) {
        return new Result<>(200, "成功", data, count);
    }

    public static Result success() {
        return new Result(200, "成功", null, 0); // count 默认为 0
    }

    public static Result error(String msg) {
        return new Result(500, msg, null, 0); // count 默认为 0
    }
}

