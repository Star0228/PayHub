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
        <!-- <el-button type="primary" class="action-button" @click.native.prevent="showApplyLoanDialog">查询汇率</el-button> -->
        <el-button type="primary" class="action-button" @click.native.prevent="showGetExchangeRateDialog">查询汇率</el-button>

        <el-button type="primary" class="action-button" @click.native.prevent="handleGetAllExchangeRates">查询所有汇率</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showTradeForexDialog">外汇交易</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetForexTradesDialog">查询用户外汇交易记录</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetForexTradeDetailsDialog">查询交易详情</el-button>



        <!-- <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button> -->
      </div>

      <el-dialog title="查询汇率" :visible.sync="GetExchangeRateVisible" width="50%" center>
        <el-form ref="GetExchangeRateFormRef" :model="GetExchangeRateForm" label-width="100px">
          <el-form-item label="指定货币对" prop="currencyPair" :rules="[ { required: true, message: '请输入指定货币对', trigger: 'blur' } ]">
            <el-input v-model="GetExchangeRateForm.currencyPair" placeholder="请输入指定货币对" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="GetExchangeRateVisible = false">取消</el-button>
          <el-button type="primary" @click="handleGetExchangeRate">查询</el-button>
        </div>
      </el-dialog>


      <el-dialog title="外汇交易" :visible.sync="TradeForexVisible" width="50%" center>
        <el-form ref="TradeForexFormRef" :model="TradeForexForm" label-width="100px">
          <el-form-item label="交易账户ID" prop="accountId" :rules="[ { required: true, message: '请输入交易账户ID', trigger: 'blur' } ]">
            <el-input v-model="TradeForexForm.accountId" placeholder="请输入交易账户ID" type="number"/>
          </el-form-item>
          <el-form-item label="货币对" prop="currencyPair" :rules="[ { required: true, message: '请输入货币对', trigger: 'blur' } ]">
            <el-input v-model="TradeForexForm.currencyPair" placeholder="请输入货币对" />
          </el-form-item>
          <el-form-item label="交易金额" prop="amount" :rules="[ { required: true, message: '请输入交易金额', trigger: 'blur' } ]">
            <el-input v-model="TradeForexForm.amount" placeholder="请输入交易金额" type="number"/>
          </el-form-item>
          <el-form-item label="交易类型" prop="type" :rules="[ { required: true, message: '请选择交易类型', trigger: 'change' } ]">
            <el-select v-model="TradeForexForm.type" placeholder="请选择交易类型">
              <el-option label="BUY" value="BUY"></el-option>
              <el-option label="SELL" value="SELL"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="TradeForexVisible = false">取消</el-button>
          <el-button type="primary" @click="handleTradeForex">交易</el-button>
        </div>
      </el-dialog>

      <el-dialog title="查询外汇交易记录" :visible.sync="GetForexTradesVisible" width="50%" center>
      <el-form ref="GetForexTradesFormRef" :model="GetForexTradesForm" label-width="100px">
        <el-form-item label="查询账户ID" prop="accountId" :rules="[ { required: true, message: '请输入查询账户ID', trigger: 'blur' } ]">
          <el-input v-model="GetForexTradesForm.accountId" placeholder="请输入查询账户ID" type="number"/>
        </el-form-item>
      </el-form>
        
        <div slot="footer" class="dialog-footer">
          <el-button @click="GetForexTradesVisible= false">关闭</el-button>
          <el-button type="primary" @click="handleGetForexTrades">查询</el-button>
        </div>
      </el-dialog>

      <el-dialog title="查询交易详情" :visible.sync="GetForexTradeDetailsVisible" width="50%" center>
        <el-form ref="GetForexTradeDetailsFormRef" :model="GetForexTradeDetailsForm" label-width="100px">
          <el-form-item label="交易ID" prop="TradeId" :rules="[ { required: true, message: '请输入交易ID', trigger: 'blur' } ]">
            <el-input v-model="GetForexTradeDetailsForm.TradeId" placeholder="请输入交易ID" type="number"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="GetForexTradeDetailsVisible = false">取消</el-button>
          <el-button type="primary" @click="handleGetForexTradeDetails">查询</el-button>
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
import { getExchangeRate, getAllExchangeRates, tradeForex, getForexTrades, getForexTradeDetails } from "@/api/forex"; // 假设有一个获取汇率的API函数
import { status } from "nprogress";


export default {
  name: "CartPage",
  data() {
    return {
      loading: false,
      cartItems: [], // 存储购物车数据

      GetExchangeRateVisible: false,
      TradeForexVisible: false,
      GetForexTradesVisible: false,
      GetForexTradeDetailsVisible: false,
      GetExchangeRateForm: {
        currencyPair: null
      },

      TradeForexForm: {
        accountId: null,
        currencyPair: null,
        amount: null,
        type: '' // BUY or SELL
      },
      GetForexTradesForm: {
        accountId: null
      },
      GetForexTradeDetailsForm: {
        TradeId: null
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

    showGetExchangeRateDialog() {
      this.GetExchangeRateVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetExchangeRateFormRef) {
        this.$refs.GetExchangeRateFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.GetExchangeRateForm = { // 或者手动重置
        currencyPair: null
      };
    },

    showTradeForexDialog() {
      this.TradeForexVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.TradeForexFormRef) {
        this.$refs.TradeForexFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.TradeForexForm = { // 或者手动重置
        accountId: null,
        currencyPair: null,
        amount: null,
        type: ''
      };
    },
    showGetForexTradesDialog(){
      this.GetForexTradesVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetForexTradesFormRef) {
        this.$refs.GetForexTradesFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.GetForexTradesForm = { // 或者手动重置
        accountId: null
      };
    },
    showGetForexTradeDetailsDialog() {
      this.GetForexTradeDetailsVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetForexTradeDetailsFormRef) {
        this.$refs.GetForexTradeDetailsFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.GetForexTradeDetailsForm = { // 或者手动重置
        TradeId: null
      };
    },




    handleGetAllExchangeRates(){
      console.log("GetAllExchangeRatesForm:"
      );
      // 调用查询所有汇率的API
      getAllExchangeRates()
        .then(response => {
          console.log('GetAllExchangeRates API response received:', response);
          if (response) {
            this.$message.success('查询所有汇率成功！');
            // console.log('GetAllExchangeRates API response:', response);
            for (const rate of response) {
              console.log("foreignExchangeId:", rate.foreignExchangeId,
              "currencyPair:", rate.currencyPair,
              "currentRate:", rate.currentRate);
            }
            // 处理查询结果
            // 例如，将结果存储在一个变量中以供显示

            this.GetExchangeRateVisible = false; // 关闭对话框

          } else {
            console.log('GetAllExchangeRates API response is empty or undefined.');
            this.$message.error('查询所有汇率失败');
            // 打印response以便调试
            console.error('GetAllExchangeRates API response:', response);
          }
        })
        .catch(error => {
          console.log('GetAllExchangeRates API error:', error);
          console.error('GetAllExchangeRates error:', error);
          this.$message.error('查询所有汇率失败，请稍后再试！');
        });
    },


    handleGetExchangeRate() {
      console.log("GetExchangeRateForm:", 
      "Currency Pair:", this.GetExchangeRateForm.currencyPair
      );
      if(!this.GetExchangeRateForm.currencyPair) {
        this.$message.error('请输入指定货币对！');
      } else {
        const payload = {
          currencyPair: this.GetExchangeRateForm.currencyPair
        };
        console.log('Payload for GetExchangeRate:', payload);

        // 调用查询汇率的API
        // 这里假设有一个名为 getExchangeRate 的API函数
        getExchangeRate(this.GetExchangeRateForm.currencyPair)
          .then(response => {
            console.log('GetExchangeRate API response received:', response);
            if (response) {
              this.$message.success('查询汇率成功！');
              console.log('GetExchangeRate API response:', response.currencyPair, response.exchangeId, response.rate);
              // 处理查询结果
              // 例如，将结果存储在一个变量中以供显示

              this.GetExchangeRateVisible = false; // 关闭对话框

            } else {
              console.log('GetExchangeRate API response is empty or undefined.');
              this.$message.error('查询汇率失败');
              // 打印response以便调试
              console.error('GetExchangeRate API response:', response);
            }
          })
          .catch(error => {
            console.log('GetExchangeRate API error:', error);
            console.error('GetExchangeRate error:', error);
            this.$message.error('查询汇率失败，请稍后再试！');
          });
      }
    },

    handleTradeForex() {
      console.log("TradeForexForm:", 
      "Account ID:", this.TradeForexForm.accountId,
      "Currency Pair:", this.TradeForexForm.currencyPair,
      "Amount:", this.TradeForexForm.amount,
      "Type:", this.TradeForexForm.type
      );
      if (!this.TradeForexForm.accountId || !this.TradeForexForm.currencyPair || !this.TradeForexForm.amount || !this.TradeForexForm.type) {
        this.$message.error('请填写所有必填字段！');
        return;
      }
      const payload = {
        accountId: this.TradeForexForm.accountId,
        currencyPair: this.TradeForexForm.currencyPair,
        amount: this.TradeForexForm.amount,
        type: this.TradeForexForm.type
      };
      console.log('Payload for TradeForex:', payload);

      // 调用外汇交易的API
      tradeForex(payload)
        .then(response => {
          console.log('TradeForex API response received:', response);
          if (response) {
            this.$message.success('外汇交易成功！');
            console.log('TradeForex API response:', response.tradeId, response.accountId, response.currencyPair, response.amount, response.exchangedAmount, response.exchangeRate, response.tradeType, response.status, response.tradeTime);
            // 处理交易结果
            // 例如，将结果存储在一个变量中以供显示

            this.TradeForexVisible = false; // 关闭对话框

          } else {
            console.log('TradeForex API response is empty or undefined.');
            this.$message.error('外汇交易失败');
            // 打印response以便调试
            console.error('TradeForex API response:', response);
          }
        })
        .catch(error => {
          console.log('TradeForex API error:', error);
          console.error('TradeForex error:', error);
          this.$message.error('外汇交易失败，请稍后再试！');
        });
    },

    handleGetForexTrades(){
      console.log("GetForexTradesForm:",
      "Account ID:", this.GetForexTradesForm.accountId
      );
      if (!this.GetForexTradesForm.accountId) {
        this.$message.error('请输入查询账户ID！');
        return;
      }
      const payload = {
        accountId: this.GetForexTradesForm.accountId
      };
      console.log('Payload for GetForexTrades:', payload);
      // 调用查询外汇交易记录的API
      getForexTrades(this.GetForexTradesForm.accountId)
        .then(response => {
          console.log('GetForexTrades API response received:', response);
          if (response) {
            this.$message.success('查询外汇交易记录成功！');
            // 处理查询结果
            // 例如，将结果存储在一个变量中以供显示

            for (const trade of response) {
              console.log("foreignExchangeTradeId:", trade.foreignExchangeTradeId,
              "accountId:", trade.accountId,
              "foreignExchangeId:", trade.foreignExchangeId,
              "currencyPair:", trade.currencyPair,
              "amount:", trade.amount,
              "tradeType:", trade.tradeType,
              "status:", trade.status,
              "tradeTime:", trade.tradeTime,
              "exchangedAmount:", trade.exchangedAmount,
              "exchangeRate:", trade.exchangeRate);
            }

            this.GetForexTradesVisible = false; // 关闭对话框

          } else {
            console.log('GetForexTrades API response is empty or undefined.');
            this.$message.error('查询外汇交易记录失败');
            // 打印response以便调试
            console.error('GetForexTrades API response:', response);
          }
        })
        .catch(error => {
          console.log('GetForexTrades API error:', error);
          console.error('GetForexTrades error:', error);
          this.$message.error('查询外汇交易记录失败，请稍后再试！');
        });
    },

    handleGetForexTradeDetails(){
      console.log("GetForexTradeDetailsForm:",
      "Trade ID:", this.GetForexTradeDetailsForm.TradeId
      );
      if (!this.GetForexTradeDetailsForm.TradeId) {
        this.$message.error('请输入交易ID！');
        return;
      }
      const payload = {
        TradeId: this.GetForexTradeDetailsForm.TradeId
      };
      console.log('Payload for GetForexTradeDetails:', payload);
      // 调用查询外汇交易详情的API
      getForexTradeDetails(this.GetForexTradeDetailsForm.TradeId)
        .then(response => {
          console.log('GetForexTradeDetails API response received:', response);
          if (response) {
            this.$message.success('查询外汇交易详情成功！');
            // 处理查询结果
            // 例如，将结果存储在一个变量中以供显示

            console.log("foreignExchangeTradeId:", response.foreignExchangeTradeId,
              "accountId:", response.accountId,
              "foreignExchangeId:", response.foreignExchangeId,
              "currencyPair:", response.currencyPair,
              "amount:", response.amount,
              "tradeType:", response.tradeType,
              "status:", response.status,
              "tradeTime:", response.tradeTime,
              "exchangedAmount:", response.exchangedAmount,
              "exchangeRate:", response.exchangeRate);

            this.GetForexTradeDetailsVisible = false; // 关闭对话框

          } else {
            console.log('GetForexTradeDetails API response is empty or undefined.');
            this.$message.error('查询外汇交易详情失败');
            // 打印response以便调试
            console.error('GetForexTradeDetails API response:', response);
          }
        })
        .catch(error => {
          console.log('GetForexTradeDetails API error:', error);
          console.error('GetForexTradeDetails error:', error);
          this.$message.error('查询外汇交易详情失败，请稍后再试！');
        });

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
