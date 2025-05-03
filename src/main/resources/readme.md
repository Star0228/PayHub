## 通用说明

> 暂未启用 jwt
>
> 后台本地测试需要执行 resources/sql

### 基础URL

所有API的基础URL为：`/api`

### 响应格式

所有API的响应均为JSON格式，遵循以下结构：

```json
{
  "code": 0,        // 0表示成功，非0表示错误
  "msg": "成功",     // 响应消息
  "data": {},       // 响应数据，具体格式根据接口不同而变化
  "count": 0        // 数据计数，用于分页或表示数据项数量
}
```





## 账户管理API

### 1. 账户注册

**描述**: 注册新账户

**请求方法**: POST

**请求路径**: `/account/register`

**请求参数**:

```json
{
  "username": "testuser",    // 必填，用户名至少6个字符
  "password": "password123", // 必填，密码
  "email": "zjuheadmaster@zju.edu.cn", // 必填，有效邮箱格式
  "address": "367", // 选填，地址
  "gender": "男",         // 选填，性别
  "occupation": "man",   // 选填，职业
  "phoneNumber": 114514, // 选填，电话号码
  "annualIncome": 1000000.00   // 选填，年收入
}
```

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "accountId": 1000001  // 后台分配的账户ID !!! 重要
  },
  "count": 1
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "用户名已存在", // 或其他错误信息
  "data": null,
  "count": 0
}
```



### 2. 账户登录

**描述**: 账户登录认证

**请求方法**: POST

**请求路径**: `/account/login`

**请求参数**:

```json
{
  "accountId": 1000001,    // 必填
  "username": "man",   // 必填
  "password": "123" // 必填，密码
}
```

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "token": "xxx", // JWT认证令牌，可忽略
    "accountId": 1000001 
  },
  "count": 1
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "用户名不存在", // 或 "密码错误", "账户ID不存在"等
  "data": null,
  "count": 0
}
```

### 3. 重置密码

**描述**: 重置账户密码

**请求方法**: POST

**请求路径**: `/account/reset-password`

**请求参数**:

```json
{
  "username": "man",    // 必填
  "password": "newpassword123" // 必填
}
```

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": null,
  "count": 0
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "用户名不存在",
  "data": null,
  "count": 0
}
```





## 交易 API

### 1. 转账

**描述**: 从一个账户转账到另一个账户

**请求方法**: POST

**请求路径**: `/transaction/transfer`

**请求头**: 需要包含Authorization令牌

**请求参数**:

```json
{
  "fromAccountId": 1000001,  // 必填，转出账户ID
  "toAccountId": 1000002,    // 必填，转入账户ID
  "amount": 100.00,          // 必填，转账金额（大于0）
  "currency": "CNY",         // 选填，币种，默认"CNY"
  "description": "转账测试"   // 选填，交易描述
}
```

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 1,
    "transactionId": "3f7c8d9e6a5b2c1d",
    "fromAccountId": 1000001,
    "toAccountId": 1000002,
    "amount": 100.00,
    "currency": "CNY",
    "status": "SUCCESS",
    "type": "TRANSFER",
    "description": "转账测试",
    "createdAt": "xxx",
    "completedAt": "xxx"
  },
  "count": 0
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "转账失败: 余额不足",
  "data": null,
  "count": 0
}
```

### 2. 查询账户余额

**描述**: 查询指定账户的余额

**请求方法**: GET

**请求路径**: `/transaction/balance/{accountId}`

**请求参数**:

- `accountId`: 路径参数，账户ID
- `currency`: 查询参数，币种，默认"CNY"

**请求示例**: `/transaction/balance/1000001?currency=CNY`

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 1,
    "accountId": 1000001,
    "balance": 2500.00,
    "currency": "CNY",
    "updatedAt": "xxx"
  },
  "count": 1
}
```

**账户无余额记录时响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "accountId": 1000001,
    "balance": 0,
    "currency": "CNY"
  },
  "count": 1
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "查询余额失败: 系统错误",
  "data": null,
  "count": 0
}
```

### 3. 查询交易记录

> 目前查到的应该 type 均为 transfer

**描述**: 查询账户的交易历史记录

**请求方法**: GET

**请求路径**: `/transaction/records/{accountId}`

**请求参数**:

- `accountId`: 路径参数，账户ID

**请求示例**: `/transaction/records/1000001`

**成功响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": [ // 本质为一个 list
    {
      "id": 1,
      "transactionId": "3f7c8d9e6a5b2c1d",
      "fromAccountId": 1000001,
      "toAccountId": 1000002,
      "amount": 100.00,
      "currency": "CNY",
      "status": "SUCCESS",
      "type": "TRANSFER",
      "description": "转账测试",
      "createdAt": "",
      "completedAt": ""
    },
    {
      "id": 2,
      "transactionId": "3f7c8d9e6a5b2c1c",
      "fromAccountId": 1000002,
      "toAccountId": 1000001,
      "amount": 100.00,
      "currency": "CNY",
      "status": "SUCCESS",
      "type": "TRANSFER",
      "description": "转账测试",
      "createdAt": "",
      "completedAt": ""
    }
  ],
  "count": 2 // list.size()
}
```

**无交易记录时响应**:

```json
{
  "code": 0,
  "msg": "成功",
  "data": [],
  "count": 0
}
```

**错误响应**:

```json
{
  "code": 1,
  "msg": "查询交易记录失败: 系统错误",
  "data": null,
  "count": 0
}
```



## 状态码说明

- `code: 0` - 操作成功
- `code: 1` - 业务错误（如余额不足、用户不存在等）
- 其他HTTP状态码：
  - `200 OK` - 请求成功
  - `400 Bad Request` - 请求参数错误
  - `401 Unauthorized` - 未授权（未提供有效的JWT令牌）
  - `500 Internal Server Error` - 服务器内部错误



## 注意事项

1. 所有金额参数必须大于0

2. 转账操作需确保转出账户有足够余额

3. 账户ID是一个从1000000开始自增的唯一标识符

   