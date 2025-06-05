<template>
  <div class="login-container">
    <el-form ref="loginForm" class="login-form" auto-complete="on" label-position="left">
      <div class="cover-image">
        <img src="@/icons/svg/cat.jpg" alt="PayHub Cover" class="cover-img">
      </div>

      <div class="example">
        <h1 class='title'>PayHub</h1>
        <h2 class='title'>智慧银行系统</h2>
        <h3 class="title">软件工程第三小组出品</h3>
      </div>

      <div class="form-area">
        <el-form :model="loginForm" class="login-form">
          <!-- <el-form-item label="用户角色">
            <el-select v-model="loginForm.id" placeholder="请选择用户角色" class="login-select" @change="selectGet">
              <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              >
                <span style="float: left">{{ item.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.id }}</span>
              </el-option>
            </el-select>
          </el-form-item> -->
            <el-form-item label="ID">
                <el-input type="id" v-model="loginForm.id" placeholder="请输入用户ID"></el-input>
            </el-form-item>
            <el-form-item label="用户">
                <el-input type="username" v-model="loginForm.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="密码">
                <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
            </el-form-item>
        </el-form>
      </div>
      
      <div class="button-container">
        <el-button :loading="loading" type="primary" class="action-button" @click.native.prevent="handleLogin">登录</el-button>
        <el-button type="success" class="action-button" @click.native.prevent="showRegisterDialog">注册</el-button>
        <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button>
      </div>

      <el-dialog title="重置密码" :visible.sync="resetPasswordDialogVisible" width="50%" center>
        </el-dialog>

      <el-dialog title="用户注册" :visible.sync="registerDialogVisible" width="50%" center>
        <el-form ref="registerFormRef" :model="registerForm" label-width="100px">
          <el-form-item label="用户名" prop="username"> 
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名（至少6个字符）"
              @input="checkUsernameLength"
            ></el-input>
            <span v-if="usernameError" class="error-message">用户名长度至少为6个字符</span>
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              type="password"
              v-model="registerForm.password"
              placeholder="请输入密码（至少6个字符）"
              @input="checkPasswordLength"
            ></el-input>
            <span v-if="passwordError" class="error-message">密码长度至少为6个字符</span>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <div class="email-input-wrapper">
              <el-input
                v-model="registerForm.emailPrefix"
                placeholder="请输入邮箱前缀"
                style="width: 45%; margin-right: 10px;"
                @input="updateFullEmail"
              ></el-input>
              <el-select
                v-model="registerForm.emailSuffix"
                @change="updateFullEmail"
                placeholder="选择邮箱后缀"
                style="width: 35%;"
              >
                <el-option
                  v-for="suffix in emailList"
                  :key="suffix"
                  :label="suffix"
                  :value="suffix"
                ></el-option>
              </el-select>
            </div>
            <el-input v-model="registerForm.email" style="display:none;"></el-input>
             <div v-if="registerForm.emailPrefix && registerForm.emailSuffix" style="margin-top: 5px; color: #909399;">
                完整邮箱: {{ registerForm.emailPrefix + registerForm.emailSuffix }}
             </div>
          </el-form-item>

          <el-form-item label="地址" prop="address">
            <el-input v-model="registerForm.address" placeholder="请输入地址（选填）"></el-input>
          </el-form-item>

          <el-form-item label="性别" prop="gender">
            <el-select v-model="registerForm.gender" placeholder="请选择性别（选填）" clearable>
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="职业" prop="occupation">
            <el-input v-model="registerForm.occupation" placeholder="请输入职业（选填）"></el-input>
          </el-form-item>

          <el-form-item label="电话号码" prop="phoneNumber">
            <el-input type="tel" v-model="registerForm.phoneNumber" placeholder="请输入电话号码（选填）"></el-input>
          </el-form-item>

          <el-form-item label="年收入" prop="annualIncome">
            <el-input type="number" v-model.number="registerForm.annualIncome" placeholder="请输入年收入（选填）">
                <template slot="append">元</template>
            </el-input>
          </el-form-item>

          <el-form-item label="用户类型" prop="userFlag">
            <el-select v-model="registerForm.userFlag" placeholder="请选择用户类型" style="width: 100%;">
              <el-option label="普通用户" :value="1"></el-option>
              <el-option label="管理员" :value="2"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="验证码" prop="verificationCode">
            <el-input v-model="registerForm.verificationCode" placeholder="请输入验证码" style="width: calc(100% - 120px); margin-right:10px;"></el-input>
            <el-button type="primary" @click="sendVerificationCodeForRegister" style="width: 110px;">发送验证码</el-button>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRegister">注册</el-button>
        </div>
      </el-dialog>
    </el-form>
  </div>
</template>

<script>
import { queryAllUsers, registerUser, loginUser, resetPassword, sendCode} from '@/api/account'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      redirect: undefined,
      userList: [],
      loginForm: {
        id: '',       // This will be accountId
        username: '', // To store the username
        password: ''
      },
      resetPasswordDialogVisible: false,
      resetPasswordForm: {
        email: '',
        verificationCode: '',
        newPassword: ''
      },
      registerDialogVisible: false,
      // 更新 registerForm 以包含所有新字段
      registerForm: {
        username: '', // API要求的用户名 (原 registerForm.id)
        password: '',
        emailPrefix: '',
        emailSuffix: '',
        email: '', // 完整邮箱，将在提交前组合
        address: '',     // 新增：地址
        gender: '',      // 新增：性别
        occupation: '',  // 新增：职业
        phoneNumber: '', // 新增：电话号码 (作为字符串处理以支持各种格式)
        annualIncome: null, // 新增：年收入 (null 或 number)
        userFlag: 1, // 新增：用户类型，默认为普通用户
        verificationCode: ''
      },
      emailList: ['@gmail.com', '@yahoo.com', '@outlook.com', '@qq.com', '@163.com', '@zju.edu.cn'],
      usernameError: false, // 原 userIdError
      passwordError: false
      // 可以为新字段添加对应的rules和error标志位，如果需要更复杂的即时验证
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    queryAllUsers().then(response => {
      if (response) {
        this.userList = response
        // this.$message.success('数据加载成功') // 考虑是否真的需要此消息
      }
    })
  },

  methods: {
    handleLogin() {
      // 1. 在这里添加日志，确认方法被调用以及表单数据是什么
      console.log('handleLogin called. Form data:', 
        'ID:', this.loginForm.id, 
        'Username:', this.loginForm.username, 
        'Password:', this.loginForm.password
      );

      // 2. 检查这个 IF 条件 - 确保所有必填字段都有值
      if (this.loginForm.id && this.loginForm.username && this.loginForm.password) {
        console.log('Initial condition (ID, Username and Password exist) met.'); // 确认条件是否满足
        this.loading = true;
        
        const payload = {
          accountId: parseInt(this.loginForm.id, 10), // 确保转换为数字类型
          username: this.loginForm.username,
          password: this.loginForm.password
        };
        console.log('Payload to be sent:', payload); // 查看将要发送的数据

        // 调用API
        loginUser(payload).then(response => {
          console.log('Login API response received:', response); // 查看原始响应
          
          if (response) { 
            this.$message.success('登录成功！');
            const storePayload = {
              token: response.token, // 假设 token 在 response.data 中
              userFlag: response.userFlag,
              accountId: this.loginForm.id,
              username: this.loginForm.username
              // password: this.loginForm.password
            };
            
            this.$store.dispatch('user/login', storePayload)
  .then(async result => {
    console.log('Vuex login成功，返回结果:', result);
    
    // 检查用户状态
    console.log('当前用户状态:', {
      token: this.$store.state.user.token,
      userId: this.$store.state.user.userId,
      username: this.$store.state.user.userName,
      roles: this.$store.state.user.roles
    });
    
    // 如果需要生成路由，手动触发路由生成
    if (result.needRouteGeneration) {
      try {
        console.log('开始手动生成路由...');
        
        // 获取用户角色
        const roles = await this.$store.dispatch('user/getInfo');
        console.log('获取到的用户角色:', roles);
        
        // 生成可访问路由
        const accessRoutes = await this.$store.dispatch('permission/generateRoutes', roles);
        console.log('生成的可访问路由:', accessRoutes);
        
        // 动态添加路由
        this.$router.addRoutes(accessRoutes);
        console.log('路由添加完成');
        
        // 跳转到指定路径
        this.$router.push({ path: result.redirectPath });
      } catch (error) {
        console.error('路由生成失败:', error);
        this.$router.push({ path: '/deposit' });
      }
    } else {
      // 跳转到指定路径
      if (result.redirectPath) {
        this.$router.push({ path: result.redirectPath });
      } else {
        this.$router.push({ path: '/deposit' });
      }
    }
  })
  .catch(storeError => {
    console.error('Vuex dispatch error after login:', storeError);
    this.$message.error('登录状态处理失败');
  });
          } else {
            this.$message.error(response.msg || '登录失败，请检查您的凭据。');
          }
        }).catch((error) => {
          console.error("Login API call error:", error);
          // 尝试从 error 对象中获取更具体的错误信息
          const errorMsg = error.response && error.response.data && error.response.data.msg 
                            ? error.response.data.msg 
                            : '登录请求失败，请检查网络或联系管理员。';
          this.$message.error(errorMsg);
        }).finally(() => {
          // 确保 loading 状态在所有情况下都被重置
          this.loading = false;
        });
      } else {
        console.log('Initial condition NOT met.');
        // 修改提示信息以匹配所有必填字段
        this.$message.error('请输入账户ID、用户名和密码'); 
      }
    },
    selectGet(userId) {
      this.loginForm.id = userId
    },
    showRegisterDialog() {
      this.registerDialogVisible = true
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.registerFormRef) {
        this.$refs.registerFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.registerForm = { // 或者手动重置
        username: '',
        password: '',
        emailPrefix: '',
        emailSuffix: '',
        email: '',
        address: '',
        gender: '',
        occupation: '',
        phoneNumber: '',
        annualIncome: null,
        userFlag: 1, // 重置时设为默认值
        verificationCode: ''
      };
      this.usernameError = false;
      this.passwordError = false;
    },
    updateFullEmail() { // 当邮箱前缀或后缀改变时，更新完整邮箱
      if (this.registerForm.emailPrefix && this.registerForm.emailSuffix) {
        this.registerForm.email = this.registerForm.emailPrefix + this.registerForm.emailSuffix;
      } else {
        this.registerForm.email = '';
      }
    },
    validateEmail(email) { // 简单的邮箱格式验证
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },
    validateEmailPrefix(prefix) { // 您之前的验证逻辑
      const regex = /^[a-zA-Z0-9][a-zA-Z0-9._]*[a-zA-Z0-9]$/;
      return regex.test(prefix);
    },
    checkUsernameLength() { // 原 checkUserIdLength
      this.usernameError = this.registerForm.username.length < 6;
    },
    checkPasswordLength() {
      this.passwordError = this.registerForm.password.length < 6;
    },
    sendVerificationCode() { // 重置密码的验证码
      if (!this.resetPasswordForm.email) {
          this.$message.error('请输入邮箱地址');
          return;
      }
      if (!this.validateEmail(this.resetPasswordForm.email)) {
          this.$message.error('请输入有效的邮箱地址');
          return;
      }
      const codePayload = {
        email: this.resetPasswordForm.email
        // verificationCode: this.resetPasswordForm.verificationCode
      };
      sendCode(codePayload).then(response => {
        // 假设 sendCode 返回类似 { code: 0, msg: "验证码已发送" }
        // 您的代码是 if (response === '验证码已发送')
        if (response === null) { // 或者 if(response && response.code === 0)
          this.$message.success('验证码发送成功')
        } else {
          // const errorMsg = response && response.msg ? response.msg : '验证码发送失败';
          this.$message.error('验证码发送失败'); // 或者使用 errorMsg
        }
      }).catch(() => {
        this.$message.error('验证码发送请求失败')
      })
    },
    sendVerificationCodeForRegister() { // 注册的验证码
      this.updateFullEmail(); // 确保邮箱已组合
      if (!this.registerForm.email) {
          this.$message.error('请填写完整的邮箱信息');
          return;
      }
      if (!this.validateEmail(this.registerForm.email)) {
          this.$message.error('请输入有效的邮箱地址');
          return;
      }
      sendCode(this.registerForm.email).then(response => {
        // console.log('验证码发送响应:', response);
        // if (response === '验证码已发送') { //  或者 if(response && response.code === 0)
          this.$message.success('验证码发送成功')
        // } else {
        //   this.$message.error(response.msg)
        // }
      }).catch(() => {
        this.$message.error('验证码发送请求失败')
      })
    },
    handleResetPassword() {
      // ... 重置密码逻辑保持不变, 但也应适配新的API响应结构 ...
      // 假设 resetPassword API 返回类似 { code: 0, msg: "密码重置成功" }
      resetPassword(this.resetPasswordForm).then(response => {
        if (response === '密码重置成功') { // 或者 if(response && response.code === 0)
          this.$message.success('密码重置成功')
          this.resetPasswordDialogVisible = false
        } else {
          this.$message.error('密码重置失败')
        }
      }).catch(() => {
        this.$message.error('密码重置请求失败')
      })
    },
    handleRegister() {
      // 预检查 (确保引用存在)
      // if (this.$refs.registerFormRef) {
      //   this.$refs.registerFormRef.validate(valid => { // Element UI 表单验证
      //     if (valid) {
      //       // 真正的提交逻辑
      //     } else {
      //       this.$message.error('请检查表单输入');
      //       return false;
      //     }
      //   });
      // } else {
      //   // 不使用 Element UI 的 validate, 手动校验
      // }

      // 手动校验核心字段 (因为Element UI的validate需要prop和rules配置)
      if (!this.registerForm.username || this.registerForm.username.length < 6) {
        this.$message.error('用户名长度必须至少为6个字符');
        this.usernameError = true; // 确保错误状态被设置
        return;
      }
      this.usernameError = false; // 清除错误状态

      if (!this.registerForm.password || this.registerForm.password.length < 6) {
        this.$message.error('密码长度必须至少为6个字符');
        this.passwordError = true; // 确保错误状态被设置
        return;
      }
      this.passwordError = false; // 清除错误状态
      
      this.updateFullEmail(); // 确保 registerForm.email 是最新的
      if (!this.registerForm.emailPrefix || !this.registerForm.emailSuffix) {
        this.$message.error('请填写完整的邮箱信息');
        return;
      }
      if (!this.validateEmail(this.registerForm.email)) {
          this.$message.error('请输入有效的邮箱地址');
          return;
      }
      // 验证码通常也应校验
      if (!this.registerForm.verificationCode) {
          this.$message.error('请输入验证码');
          return;
      }

      // 移除不必要的旧字段，构建payload
      const payload = {
        username: this.registerForm.username,
        password: this.registerForm.password,
        email: this.registerForm.email,
        // 选填字段：如果为空字符串或者null，根据后端要求决定是否发送
        // 如果后端不接受null或空字符串作为可选字段的有效值，则需要条件性添加
        address: this.registerForm.address || undefined, //  发送undefined，axios会忽略它，或者发送空字符串 ""
        gender: this.registerForm.gender || undefined,
        occupation: this.registerForm.occupation || undefined,
        phoneNumber: this.registerForm.phoneNumber ? String(this.registerForm.phoneNumber) : undefined, // API期望number，但输入可能是string
        annualIncome: this.registerForm.annualIncome === null || this.registerForm.annualIncome === '' ? undefined : Number(this.registerForm.annualIncome),
        userFlag: this.registerForm.userFlag || 1 // 新增：确保userFlag有值
        // verificationCode: this.registerForm.verificationCode, // 根据API是否需要在注册接口提交验证码
      };
      
      // 清理 undefined 字段，避免发送它们 (如果后端不希望接收值为 undefined 的键)
      Object.keys(payload).forEach(key => {
        if (payload[key] === undefined) {
          delete payload[key];
        }
      });

      // 假设 registerUser API 也需要验证码，如果需要，则添加到 payload
      // payload.verificationCode = this.registerForm.verificationCode;

      console.log("Registering payload:", payload); // 调试

      registerUser(payload).then(response => { // 修改了传入的参数
        // 根据新的响应格式处理
        if (response) {
          this.$message.success(response.msg || '注册成功！');
          console.log('注册成功，账户ID:', response.data.accountId); // 使用新的 accountId
          this.registerDialogVisible = false;
          // 可以在这里做一些额外操作，比如提示用户accountId
          this.$alert(`注册成功！您的账户ID是: ${response.data.accountId}`, '注册成功', {
            confirmButtonText: '确定',
            callback: action => {
              // 可选：注册成功后清空登录表单或做其他跳转
            }
          });
        } else {
          this.$message.error(response.msg || '注册失败，请稍后再试。');
        }
      }).catch(error => {
        console.error("Registration error:", error);
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                         ? error.response.data.msg 
                         : '注册请求失败';
        // this.$message.error(errorMsg);
      });
    },
    showResetPasswordDialog() {
      this.resetPasswordDialogVisible = true;
      // 也可以在这里添加表单重置逻辑
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100%;
  width: 100%;
  height: 100%;
  position: fixed;
  background-color: #f2f2f2;
  background-size: 100% 100%;
  overflow: hidden;
  display: flex; /* 确保flex布局对内部元素生效 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */

  .login-form { // login-form 现在是 flex item
    position: relative; // 改为relative，因为父级已经是flex用于居中
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 15px 35px; // 调整padding
    margin: 0; // 移除auto margin，因为父级flex会处理居中
    background: #fff; // 给表单一个背景色，以便在灰色背景上显示
    border-radius: 8px; // 可选：圆角
    box-shadow: 0 0 10px rgba(0,0,0,0.1); // 可选：阴影
  }

  .button-container {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    margin-bottom: 20px; // 调整间距
    margin-top: 20px; // 按钮与表单内容间距
  }

  .action-button {
    flex: 1;
  }

  .cover-image {
    width: 150px; // 调整大小
    height: 150px;
    margin: 0 auto 20px auto; // 上下间距，左右自动居中

    .cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 50%;
    }
  }

  .example {
    text-align: center;
    margin-bottom: 20px;
    .title {
      margin: 5px 0;
      color: #333;
    }
    h1.title { font-size: 28px; color: #409EFF; }
    h2.title { font-size: 20px; }
    h3.title { font-size: 14px; color: #909399; }
  }

  .form-area {
    // 可以移除，如果 login-form 直接包含 el-form
  }

  .login-select {
    width: 100%; // 让下拉框撑满
  }

  .error-message {
    color: #F56C6C;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
    position: absolute;
    top: 100%;
    left: 0;
  }
  .el-form-item {
    margin-bottom: 22px; // 统一表单项间距
  }
  /* 针对注册弹窗内的表单项调整 */
  .el-dialog .el-form-item {
     margin-bottom: 22px;
  }
  .email-input-wrapper {
    display: flex;
    align-items: center;
  }
}
</style>