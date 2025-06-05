<template>
  <div class="container">
    <h2 class="title">贷款业务</h2>

    <!-- 按钮组 -->
    <!-- <div class="button-group">

      <el-button type="primary" @click="clearCart">贷款申请</el-button>
      <el-button type="primary" @click="clearCart">贷款审批</el-button>
      <el-button type="primary" @click="clearCart">贷款还款</el-button>
      <el-button type="primary" @click="clearCart">查询用户贷款申请</el-button>
      <el-button type="primary" @click="clearCart">查询用户贷款记录</el-button>
      <el-button type="primary" @click="clearCart">逾期检查（管理员功能）</el-button>

    </div> -->

    <div class="button-container">
        <!-- <el-button :loading="loading" type="primary" class="action-button" @click.native.prevent="handleLogin">登录</el-button> -->
        <!-- <el-button type="success" class="action-button" @click.native.prevent="showRegisterDialog">注册</el-button> -->
        <el-button type="primary" class="action-button" @click.native.prevent="showApplyLoanDialog">贷款申请</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showApproveLoanDialog">贷款审批</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showRepayLoanDialog">贷款还款</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetLoanApplicationsDialog">查询用户贷款申请</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetLoansDialog">查询用户贷款记录</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="handleCheckOverdue">逾期检查（管理员功能）</el-button>

        <!-- <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button> -->
      </div>

      <el-dialog title="贷款申请" :visible.sync="ApplyLoanVisible" width="50%" center>
        <el-form ref="ApplyLoanFormRef" :model="ApplyLoanForm" label-width="100px">

          <el-form-item label="账户ID" prop="accountId"> 
            <el-input
              v-model="ApplyLoanForm.accountId"
              placeholder="请输入账户ID"
              type="number"
            />
          </el-form-item>
          <el-form-item label="贷款金额" prop="amountOfMoney"> 
            <el-input
              v-model="ApplyLoanForm.amountOfMoney"
              placeholder="请输入贷款金额"
              type="number"
            />
          </el-form-item>

          <el-form-item label="申请日期" prop="applicationDate"> 
            <el-date-picker
              v-model="ApplyLoanForm.applicationDate"
              type="date"
              placeholder="选择申请日期"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="贷款期限（月）" prop="term"> 
            <el-input
              v-model="ApplyLoanForm.term"
              placeholder="请输入贷款期限（月）"
              type="number"
            />
          </el-form-item>

          <el-form-item label="贷款用途" prop="purpose"> 
            <el-input
              v-model="ApplyLoanForm.purpose"
              placeholder="请输入贷款用途"
            />
          </el-form-item>

          <el-form-item label="信用评分" prop="creditScore"> 
            <el-input
              v-model="ApplyLoanForm.creditScore"
              placeholder="请输入信用评分（可选）"
              type="number"
            />
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApplyLoanVisible = false">取消</el-button>
          <el-button type="primary" @click="handleApplyLoan">申请</el-button>
        </div>
      </el-dialog>




      
       <el-dialog title="贷款审批" :visible.sync="ApplyApproveVisible" width="50%" center>
        <el-form ref="ApproveLoanFormRef" :model="ApproveLoanForm" label-width="100px">

          <el-form-item label="申请ID" prop="applicationId"> 
            <el-input
              v-model="ApproveLoanForm.applicationId"
              placeholder="请输入申请ID"
              type="number"
            />
          </el-form-item>
          <el-form-item label="审批员ID" prop="examinerId"> 
            <el-input
              v-model="ApproveLoanForm.examinerId"
              placeholder="请输入审批员ID"
              type="number"
            />
          </el-form-item>

          <el-form-item label="审批结果" prop="result">
            <el-select v-model="ApproveLoanForm.result" placeholder="请选择审批结果" clearable>
              <el-option label="通过" value="通过"></el-option>
              <el-option label="拒绝" value="拒绝"></el-option>
              <!-- <el-option label="其他" value="其他"></el-option> -->
            </el-select>
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApproveLoanVisible = false">取消</el-button>
          <el-button type="primary" @click="handleApproveLoan">提交</el-button>
        </div>
      </el-dialog>




      <el-dialog title="贷款还款" :visible.sync="ApplyRepayVisible" width="50%" center>
        <el-form ref="RepayLoanFormRef" :model="RepayLoanForm" label-width="100px">

          <el-form-item label="贷款ID" prop="loanId"> 
            <el-input
              v-model="RepayLoanForm.loanId"
              placeholder="请输入贷款ID"
              type="number"
            />
          </el-form-item>
          <el-form-item label="还款金额" prop="amount"> 
            <el-input
              v-model="RepayLoanForm.amount"
              placeholder="请输入还款金额"
              type="number"
            />
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApplyRepayVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRepayLoan">提交</el-button>
        </div>
      </el-dialog>

      <!-- 查询用户贷款申请 -->
      <el-dialog title="查询用户贷款申请" :visible.sync="ApplyGetLoanApplicationsVisible" width="50%" center>
        <el-form ref="GetLoanApplicationsFormRef" :model="GetLoanApplicationsForm" label-width="100px">
          <!-- 在这里添加查询用户贷款申请的表单内容 -->
          <el-form-item label="账户ID" prop="accountId"> 
            <el-input
              v-model="GetLoanApplicationsForm.accountId"
              placeholder="请输入账户ID"
              type="number"
            />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApplyGetLoanApplicationsVisible = false">取消</el-button>
          <el-button type="primary" @click="handleGetLoanApplications">查询</el-button>
        </div> 
      </el-dialog>

      <!-- 查询用户贷款记录 -->
       <el-dialog title="查询用户贷款记录" :visible.sync="ApplyGetLoansVisible" width="50%" center>
        <el-form ref="GetLoansFormRef" :model="GetLoansForm" label-width="100px">
          <!-- 在这里添加查询用户贷款记录的表单内容 -->
          <el-form-item label="账户ID" prop="accountId"> 
            <el-input
              v-model="GetLoansForm.accountId"
              placeholder="请输入账户ID"
              type="number"
            />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApplyGetLoansVisible = false">取消</el-button>
          <el-button type="primary" @click="handleGetLoans">查询</el-button>
        </div>
      </el-dialog>


  </div>
</template>

<script>
// import { getCartItems, removeCartItem, updateCartItem, clearCart } from "@/api/cart";
// import { checkCart } from "@/api/cart"; // 引入 checkCart API
import { mapGetters } from 'vuex';

// 从/api/loan.js中导入API函数
import { applyLoan, approveLoan, repayLoan, getLoanApplications, getLoans, checkOverdue } from "@/api/loan";
import { status } from "nprogress";


export default {
  name: "CartPage",
  data() {
    return {
      loading: false,
      cartItems: [], // 存储购物车数据
      ApplyLoanVisible: false,
      ApplyApproveVisible: false,
      ApplyRepayVisible: false,
      ApplyGetLoanApplicationsVisible: false,
      ApplyGetLoansVisible: false,
      ApplyCheckOverdueVisible: false,
      

      registerDialogVisible: true,
      registerForm: {
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
        userFlag: 1,
        verificationCode: '',
        accountId: null,
      },
      ApplyLoanForm: {
        accountId: null,
        amountOfMoney: null,
        applicationDate: '',
        term: null,
        purpose: '',
        creditScore: null
      },
      ApproveLoanForm:{
        applicationId: null,
        examinerId: null,
        result: ''
      },
      RepayLoanForm: {
        loanId: null,
        amount: null
      },
      GetLoanApplicationsForm: {
        accountId: null
      },
      GetLoansForm: {
        accountId: null
      },
      CheckOverdueForm: {
        accountId: null
      },
      usernameError: false,
      passwordError: false,
      emailList: ['@qq.com', '@gmail.com', '@zju.edu.cn']
    };
  },
  computed: {
    ...mapGetters([
      'userId',
      'roles',
      'userName'
    ])
  },
  // created() {
  //   this.fetchCartItems();
  // },
  methods: {

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

    showApplyLoanDialog() {
      this.ApplyLoanVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.ApplyLoanFormRef) {
        this.$refs.ApplyLoanFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.ApplyLoanForm = { // 或者手动重置
        accountId: null,
        amountOfMoney: null,
        applicationDate: '',
        term: null,
        purpose: '',
        creditScore: null
      };

    },

    showApproveLoanDialog() {
      this.ApplyApproveVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.ApproveLoanFormRef) {
        this.$refs.ApproveLoanFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.ApproveLoanForm = { // 或者手动重置
        applicationId: null,
        examinerId: null,
        result: ''
      };
    },

    showRepayLoanDialog() {
      this.ApplyRepayVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.RepayLoanFormRef) {
        this.$refs.RepayLoanFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.RepayLoanForm = { // 或者手动重置
        loanId: null,
        amount: null
      };
    },
    showGetLoanApplicationsDialog() {
      this.ApplyGetLoanApplicationsVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetLoanApplicationsFormRef) {
        this.$refs.GetLoanApplicationsFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.GetLoanApplicationsForm = { // 或者手动重置
        accountId: null
      };
    },
    showGetLoansDialog() {
      this.ApplyGetLoansVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetLoansFormRef) {
        this.$refs.GetLoansFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.GetLoansForm = { // 或者手动重置
        accountId: null
      };
    },

    // showCheckOverdueDialog(){
    //   this.ApplyCheckOverdueVisible = true;
    //   // 清空上次的表单数据 (可选, 推荐)
    //   if (this.$refs.CheckOverdueFormRef) {
    //     this.$refs.CheckOverdueFormRef.resetFields(); // 如果设置了prop，可以这样重置
    //   }
    //   // this.CheckOverdueForm = { // 或者手动重置
    //   //   accountId: null
    //   // };
    // },
    handleApplyLoan(){
      // 1. 在这里添加日志，确认方法被调用以及表单数据是什么
      console.log('ApplyLoanForm:', 
      "Account ID:", this.ApplyLoanForm.accountId,
      "Amount of Money:", this.ApplyLoanForm.amountOfMoney,
      "Application Date:", this.ApplyLoanForm.applicationDate,
      "Term:", this.ApplyLoanForm.term,
      "Purpose:", this.ApplyLoanForm.purpose,
      "Credit Score:", this.ApplyLoanForm.creditScore
      );
      // 2. 检查这个 IF 条件 - 确保所有必填字段都有值
      if (!this.ApplyLoanForm.accountId || !this.ApplyLoanForm.amountOfMoney || !this.ApplyLoanForm.applicationDate || !this.ApplyLoanForm.term || !this.ApplyLoanForm.purpose) {
        console.log('Initial condition NOT met.');
        // 修改提示信息以匹配所有必填字段
        this.$message.error('请输入账户ID、贷款金额、申请日期、贷款期限和贷款用途！');
      }else{
        console.log('Initial condition (ID, Amount, Date, Term, Purpose) met.');
        const payload = {
          accountId: this.ApplyLoanForm.accountId,
          amountOfMoney: this.ApplyLoanForm.amountOfMoney,
          applicationDate: this.ApplyLoanForm.applicationDate,
          term: this.ApplyLoanForm.term,
          purpose: this.ApplyLoanForm.purpose,
          creditScore: this.ApplyLoanForm.creditScore
        };
        console.log('Payload for ApplyLoan:', payload);

        // 3. 调用 ApplyLoan API
        applyLoan(payload)
          .then(response => {
            console.log('ApplyLoan API response received:', response);
            if (response) {
              this.$message.success('贷款申请成功！');
              console.log('ApplyLoan API response:', response);
              console.log('ApplyLoan API response data:', response.applicationId, response.status);

              this.ApplyLoanVisible = false; // 关闭对话框
              const storePayload = {
                accountId: this.ApplyLoanForm.accountId,
                amountOfMoney: this.ApplyLoanForm.amountOfMoney,
                applicationDate: this.ApplyLoanForm.applicationDate,
                term: this.ApplyLoanForm.term,
                purpose: this.ApplyLoanForm.purpose,
                creditScore: this.ApplyLoanForm.creditScore,
                applicationId: response.applicationId, // 假设API返回了applicationId
                status: response.status // 假设API返回了status
              };
            } else {
              console.log('ApplyLoan API response is empty or undefined.');
              this.$message.error('贷款申请失败');
              // 打印response以便调试
              console.error('ApplyLoan API response:', response);
            }
          })
          .catch(error => {
            console.log('ApplyLoan API error:', error);
            console.error('ApplyLoan error:', error);
            this.$message.error('贷款申请失败，请稍后再试！');
          });
    }
    },



    
    handleApproveLoan(){
      console.log("ApproveLoanForm:", 
      "Application ID:", this.ApproveLoanForm.applicationId,
      "Examiner ID:", this.ApproveLoanForm.examinerId,
      "Result:", this.ApproveLoanForm.result
      );
      if(!this.ApproveLoanForm.applicationId || !this.ApproveLoanForm.examinerId || !this.ApproveLoanForm.result) {
        this.$message.error('请输入申请ID、审批员ID和审批结果！');
      } else {
        const payload = {
          applicationId: this.ApproveLoanForm.applicationId,
          examinerId: this.ApproveLoanForm.examinerId,
          result: this.ApproveLoanForm.result
        };
        console.log('Payload for ApproveLoan:', payload);

        approveLoan(payload)
          .then(response => {
            console.log('ApproveLoan API response received:', response);
            if (response) {
              this.$message.success('贷款审批成功！');
              console.log('ApproveLoan API response:', response);
              console.log('ApproveLoan API response data:', response.applicationId, response.status, response.loanId);

              this.ApplyApproveVisible = false; // 关闭对话框
              const storePayload = {
                applicationId: this.ApproveLoanForm.applicationId,
                examinerId: this.ApproveLoanForm.examinerId,
                result: this.ApproveLoanForm.result,
                applicationId: response.applicationId, // 假设API返回了applicationId
                loanId: response.loanId, // 假设API返回了loanId
                status: response.status // 假设API返回了status
              };

            } else {
              console.log('ApproveLoan API response is empty or undefined.');
              this.$message.error('贷款审批失败');
              // 打印response以便调试
              console.error('ApproveLoan API response:', response);
            }
          })
          .catch(error => {
            console.log('ApproveLoan API error:', error);
            console.error('ApproveLoan error:', error);
            this.$message.error('贷款审批失败，请稍后再试！');
          });
      }
    },


    handleRepayLoan() {
      console.log("RepayLoanForm:", 
      "Loan ID:", this.RepayLoanForm.loanId,
      "Amount:", this.RepayLoanForm.amount
      );
      if(!this.RepayLoanForm.loanId || !this.RepayLoanForm.amount) {
        this.$message.error('请输入贷款ID和还款金额！');
      } else {
        const payload = {
          loanId: this.RepayLoanForm.loanId,
          amount: this.RepayLoanForm.amount
        };
        console.log('Payload for RepayLoan:', payload);

        repayLoan(payload)
          .then(response => {
            console.log('RepayLoan API response received:', response);
            if (response) {
              this.$message.success('贷款还款成功！');
              console.log('RepayLoan API response:', response);
              // console.log('RepayLoan API response data:', response.applicationId, response.status);

              this.ApplyRepayVisible = false; // 关闭对话框

            } else {
              console.log('RepayLoan API response is empty or undefined.');
              this.$message.error('贷款还款失败');
              // 打印response以便调试
              console.error('RepayLoan API response:', response);
            }
          })
          .catch(error => {
            console.log('RepayLoan API error:', error);
            console.error('RepayLoan error:', error);
            this.$message.error('贷款还款失败，请稍后再试！');
          });
      }
    },

    handleGetLoanApplications(){
      console.log("GetLoanApplicationsForm:", 
      "Account ID:", this.GetLoanApplicationsForm.accountId
      );
      if(!this.GetLoanApplicationsForm.accountId) {
        this.$message.error('请输入账户ID！');
      } else {
        const payload = {
          accountId: this.GetLoanApplicationsForm.accountId
        };
        console.log('Payload for GetLoanApplications:', payload);

        getLoanApplications(this.GetLoanApplicationsForm.accountId)
          .then(response => {
            console.log('GetLoanApplications API response received:', response);
            if (response) {
              this.$message.success('查询用户贷款申请成功！');
              console.log('GetLoanApplications API response:', response);
              console.log('GetLoanApplications API response data:', response.loanApplicationRecordId, response.accountId, response.amountOfMoney, response.applicationDate, response.loanExaminerId, response.creditScore, response.status, response.term, response.purpose, response.createdAt, response.updatedAt);
              // 处理查询结果
              // 例如，将结果存储在一个变量中以供显示

              this.ApplyGetLoanApplicationsVisible = false; // 关闭对话框

            } else {
              console.log('GetLoanApplications API response is empty or undefined.');
              this.$message.error('查询用户贷款申请失败');
              // 打印response以便调试
              console.error('GetLoanApplications API response:', response);
            }
          })
          .catch(error => {
            console.log('GetLoanApplications API error:', error);
            console.error('GetLoanApplications error:', error);
            this.$message.error('查询用户贷款申请失败，请稍后再试！');
          });
      }
    },

    handleGetLoans(){
      console.log("GetLoansForm:", 
      "Account ID:", this.GetLoansForm.accountId
      );
      if(!this.GetLoansForm.accountId) {
        this.$message.error('请输入账户ID！');
      } else {
        const payload = {
          accountId: this.GetLoansForm.accountId
        };
        console.log('Payload for GetLoans:', payload);

        getLoans(this.GetLoansForm.accountId)
          .then(response => {
            console.log('GetLoans API response received:', response);
            if (response) {
              this.$message.success('查询用户贷款记录成功！');
              console.log('GetLoans API response:', response);
              console.log('GetLoans API response data:', response.loanId, response.loanApplicationRecordId, response.amountOfMoney, response.repaymentDate, response.interestRate, response.isOverdue, response.status, response.createdAt, response.updatedAt);
              // 处理查询结果
              // 例如，将结果存储在一个变量中以供显示

              this.ApplyGetLoansVisible = false; // 关闭对话框

            } else {
              console.log('GetLoans API response is empty or undefined.');
              this.$message.error('查询用户贷款记录失败');
              // 打印response以便调试
              console.error('GetLoans API response:', response);
            }
          })
          .catch(error => {
            console.log('GetLoans API error:', error);
            console.error('GetLoans error:', error);
            this.$message.error('查询用户贷款记录失败，请稍后再试！');
          });
      }
    },

    handleCheckOverdue() {
      console.log("CheckOverdueForm:"
      );
      // if(!this.CheckOverdueForm.accountId) {
      //   this.$message.error('请输入账户ID！');
      // } else {
        // const payload = {
        //   accountId: this.CheckOverdueForm.accountId
        // };
        // console.log('Payload for CheckOverdue:', payload);

        checkOverdue()
          .then(response => {
            console.log('CheckOverdue API response received:', response);
            if (response) {
              this.$message.success('逾期检查成功！');
              console.log('CheckOverdue API response:', response);
              // 处理逾期检查结果
              // 例如，将结果存储在一个变量中以供显示

              this.ApplyCheckOverdueVisible = false; // 关闭对话框

            } else {
              console.log('CheckOverdue API response is empty or undefined.');
              this.$message.error('逾期检查失败');
              // 打印response以便调试
              console.error('CheckOverdue API response:', response);
            }
          })
          .catch(error => {
            console.log('CheckOverdue API error:', error);
            console.error('CheckOverdue error:', error);
            this.$message.error('逾期检查失败，请稍后再试！');
          });
      // }
    }

  }
}
</script>

<style>
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  text-align: center;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 1.2rem;
  margin-top: 20px;
}

img {
  width: 100px;
  height: 100px;
  object-fit: cover;  /* 确保图片缩放时保持比例 */
}

.button-group {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
