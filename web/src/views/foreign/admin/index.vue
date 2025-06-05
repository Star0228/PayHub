<template>
  <div class="container">
    <h2 class="title">外汇业务</h2>

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
        <el-button type="primary" class="action-button" @click.native.prevent="showGetForexTradesDialog">查询用户外汇交易记录</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetForexTradeDetailsDialog">查询交易详情</el-button>



        <!-- <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button> -->
      </div>

      <!-- 汇率表格显示区域 -->
      <div class="exchange-rates-section" v-if="exchangeRates.length > 0">
        <h3 class="section-title">实时汇率</h3>
        <el-table 
          :data="exchangeRates" 
          style="width: 100%" 
          border
          stripe
          class="rates-table"
        >
          <el-table-column 
            prop="foreignExchangeId" 
            label="汇率ID" 
            width="120"
            align="center"
          />
          <el-table-column 
            prop="currencyPair" 
            label="货币对" 
            width="150"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag type="primary">{{ scope.row.currencyPair }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            prop="currentRate" 
            label="当前汇率" 
            align="center"
          >
            <template slot-scope="scope">
              <span class="rate-value">{{ formatRate(scope.row.currentRate) }}</span>
            </template>
          </el-table-column>          <el-table-column 
            label="操作" 
            width="250"
            align="center"
          >
            <template slot-scope="scope">
              <el-button 
                size="mini" 
                type="success" 
                @click="quickTrade(scope.row)"
              >
                快速交易
              </el-button>
              <el-button 
                size="mini" 
                type="danger" 
                @click="removeExchangeRate(scope.$index)"
                style="margin-left: 8px;"
              >
                删除
              </el-button>
            </template>
          </el-table-column></el-table>
      </div>

      <!-- 外汇交易记录显示区域 -->
      <div class="forex-trades-section" v-if="forexTrades.length > 0">
        <h3 class="section-title">外汇交易记录</h3>
        <el-table 
          :data="forexTrades" 
          style="width: 100%" 
          border
          stripe
          class="trades-table"
        >
          <el-table-column 
            prop="foreignExchangeTradeId" 
            label="交易ID" 
            width="100"
            align="center"
          />
          <el-table-column 
            prop="currencyPair" 
            label="货币对" 
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag type="success">{{ scope.row.currencyPair }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            prop="amount" 
            label="交易金额" 
            align="center"
          >
            <template slot-scope="scope">
              <span class="amount-value">{{ formatAmount(scope.row.amount) }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="exchangeRate" 
            label="汇率" 
            align="center"
          >
            <template slot-scope="scope">
              <span class="rate-value">{{ formatRate(scope.row.exchangeRate) }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="exchangedAmount" 
            label="兑换金额" 
            align="center"
          >
            <template slot-scope="scope">
              <span class="amount-value">{{ formatAmount(scope.row.exchangedAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="tradeType" 
            label="交易类型" 
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="scope.row.tradeType === 'BUY' ? 'success' : 'warning'">
                {{ scope.row.tradeType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            prop="status" 
            label="状态" 
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>          <el-table-column 
            prop="tradeTime" 
            label="交易时间" 
            width="180"
            align="center"
          >
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.tradeTime) }}
            </template>
          </el-table-column>
          <el-table-column 
            label="操作" 
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-button 
                size="mini" 
                type="danger" 
                @click="removeForexTrade(scope.$index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-dialog title="查询汇率" :visible.sync="GetExchangeRateVisible" width="50%" center>
        <el-form ref="GetExchangeRateFormRef" :model="GetExchangeRateForm" label-width="100px">
          <el-form-item label="指定货币对" prop="currencyPair" :rules="[ { required: true, message: '请输入指定货币对', trigger: 'blur' } ]">
            <el-input v-model="GetExchangeRateForm.currencyPair" placeholder="请输入指定货币对" />
          </el-form-item>
        </el-form>        <div slot="footer" class="dialog-footer">
          <el-button @click="GetExchangeRateVisible = false">取消</el-button>
          <el-button @click="clearGetExchangeRateForm">清空</el-button>
          <el-button type="primary" @click="handleGetExchangeRate">查询</el-button>
        </div>
      </el-dialog>      <el-dialog title="外汇交易" :visible.sync="TradeForexVisible" width="50%" center>
        <el-form ref="TradeForexFormRef" :model="TradeForexForm" label-width="100px">
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
      </el-dialog>      <el-dialog title="查询外汇交易记录" :visible.sync="GetForexTradesVisible" width="50%" center>
      <el-form ref="GetForexTradesFormRef" :model="GetForexTradesForm" label-width="100px">
        <el-form-item label="账户ID" prop="accountId" :rules="[ { required: true, message: '请输入账户ID', trigger: 'blur' } ]">
          <el-input
            v-model="GetForexTradesForm.accountId"
            placeholder="请输入要查询的账户ID"
            type="number"
          />
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

        <!-- 快速交易对话框 -->
        <el-dialog title="快速交易" :visible.sync="quickTradeDialogVisible" width="50%" center>
          <div v-if="selectedRate">
            <p><strong>货币对：</strong>{{ selectedRate.currencyPair }}</p>
            <p><strong>当前汇率：</strong>{{ formatRate(selectedRate.currentRate) }}</p>
          </div>
          <el-form ref="quickTradeFormRef" :model="quickTradeForm" label-width="100px">
            <el-form-item label="交易金额" prop="amount" :rules="[{ required: true, message: '请输入交易金额', trigger: 'blur' }]">
              <el-input v-model="quickTradeForm.amount" placeholder="请输入交易金额" type="number"/>
            </el-form-item>
            <el-form-item label="交易类型" prop="type" :rules="[{ required: true, message: '请选择交易类型', trigger: 'change' }]">
              <el-select v-model="quickTradeForm.type" placeholder="请选择交易类型">
                <el-option label="买入 (BUY)" value="BUY"></el-option>
                <el-option label="卖出 (SELL)" value="SELL"></el-option>
              </el-select>
            </el-form-item>
          </el-form>          <div slot="footer" class="dialog-footer">
            <el-button @click="quickTradeDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleQuickTrade">确认交易</el-button>
          </div>
        </el-dialog>

        <!-- 汇率查询结果对话框 -->
        <el-dialog title="汇率查询结果" :visible.sync="exchangeRateResultVisible" width="50%" center>
          <div v-if="exchangeRateResult" class="exchange-rate-result">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="汇率ID">
                <el-tag type="primary">{{ exchangeRateResult.exchangeId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="货币对">
                <el-tag type="success">{{ exchangeRateResult.currencyPair }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="汇率">
                <span class="rate-value">{{ formatRate(exchangeRateResult.rate) }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>          <div slot="footer" class="dialog-footer">
            <el-button @click="exchangeRateResultVisible = false">关闭</el-button>
            <el-button type="danger" @click="removeCurrentExchangeRate">删除此汇率</el-button>
            <el-button type="primary" @click="startQuickTradeFromResult">快速交易</el-button>
          </div>        </el-dialog>

        <!-- 交易详情查询结果对话框 -->
        <el-dialog title="交易详情查询结果" :visible.sync="tradeDetailsResultVisible" width="60%" center>
          <div v-if="tradeDetailsResult" class="trade-details-result">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="交易ID">
                <el-tag type="primary">{{ tradeDetailsResult.foreignExchangeTradeId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="账户ID">
                <el-tag>{{ tradeDetailsResult.accountId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="外汇ID">
                <el-tag>{{ tradeDetailsResult.foreignExchangeId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="货币对">
                <el-tag type="success">{{ tradeDetailsResult.currencyPair }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="交易金额">
                <span class="amount-value">{{ formatAmount(tradeDetailsResult.amount) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="兑换金额">
                <span class="amount-value">{{ formatAmount(tradeDetailsResult.exchangedAmount) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="交易汇率">
                <span class="rate-value">{{ formatRate(tradeDetailsResult.exchangeRate) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="交易类型">
                <el-tag :type="tradeDetailsResult.tradeType === 'BUY' ? 'success' : 'warning'">
                  {{ tradeDetailsResult.tradeType }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="交易状态">
                <el-tag :type="getStatusType(tradeDetailsResult.status)">
                  {{ tradeDetailsResult.status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="交易时间">
                {{ formatDateTime(tradeDetailsResult.tradeTime) }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button @click="tradeDetailsResultVisible = false">关闭</el-button>
            <el-button type="danger" @click="removeCurrentTradeDetails">删除此交易记录</el-button>
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
      cartItems: [], // 存储购物车数据      GetExchangeRateVisible: false,
      TradeForexVisible: false,
      GetForexTradesVisible: false,      GetForexTradeDetailsVisible: false,
      exchangeRateResultVisible: false,
      exchangeRateResult: null,
      tradeDetailsResultVisible: false,
      tradeDetailsResult: null,
      GetExchangeRateForm: {
        currencyPair: null
      },      TradeForexForm: {
        currencyPair: null,
        amount: null,
        type: '' // BUY or SELL
      },      GetForexTradesForm: {
        accountId: null // Add accountId for admin to query specific user's records
      },
      GetForexTradeDetailsForm: {
        TradeId: null
      },      exchangeRates: [], // 存储汇率数据
      forexTrades: [], // 存储外汇交易记录
      quickTradeDialogVisible: false,
      selectedRate: null,
      quickTradeForm: {
        amount: null,
        type: 'BUY'
      },

      usernameError: false,
      passwordError: false,
      emailList: ['@qq.com', '@gmail.com', '@zju.edu.cn']
    };
  },
  computed: {
    ...mapGetters([
      'token',
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
      }      this.TradeForexForm = { // 或者手动重置
        currencyPair: null,
        amount: null,
        type: ''
      };
    },    showGetForexTradesDialog(){
      this.GetForexTradesVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.GetForexTradesFormRef) {
        this.$refs.GetForexTradesFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }      this.GetForexTradesForm = { // 或者手动重置
        accountId: null // Reset accountId field for admin to query specific user's records
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
            this.exchangeRates = response; // 存储汇率数据

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
        console.log('Payload for GetExchangeRate:', payload);        // 调用查询汇率的API
        // 这里假设有一个名为 getExchangeRate 的API函数
        getExchangeRate(this.GetExchangeRateForm.currencyPair)
          .then(response => {
            console.log('GetExchangeRate API response received:', response);            if (response) {
              this.$message.success(`查询汇率成功！汇率ID: ${response.exchangeId}, 货币对: ${response.currencyPair}, 汇率: ${response.rate}`);
              console.log('GetExchangeRate API response:', 'exchangeId:', response.exchangeId, 'currencyPair:', response.currencyPair, 'rate:', response.rate);
              // 处理查询结果
              // 存储查询结果并显示
              this.exchangeRateResult = response;
              this.exchangeRateResultVisible = true;

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
    },    handleTradeForex() {
      console.log("TradeForexForm:", 
      "Currency Pair:", this.TradeForexForm.currencyPair,
      "Amount:", this.TradeForexForm.amount,
      "Type:", this.TradeForexForm.type,
      "User ID:", this.userId
      );
      if (!this.TradeForexForm.currencyPair || !this.TradeForexForm.amount || !this.TradeForexForm.type) {
        this.$message.error('请填写所有必填字段！');
        return;
      }
      const payload = {
        token: this.token, // 使用存储的token
        accountId: this.userId.toString(10), // 使用存储的用户ID
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
            // 刷新交易记录
            this.refreshForexTrades();

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
    },    handleGetForexTrades(){
      // First validate the form to ensure accountId is provided
      this.$refs.GetForexTradesFormRef.validate((valid) => {
        if (!valid) {
          this.$message.error('请填写完整的查询信息！');
          return;
        }
        
        console.log("GetForexTradesForm - Admin querying for Account ID:", this.GetForexTradesForm.accountId);
        
        const payload = {
          accountId: this.GetForexTradesForm.accountId // 使用管理员输入的账户ID
        };
        console.log('Payload for GetForexTrades:', payload);
        
        // 调用查询外汇交易记录的API，使用输入的账户ID
        getForexTrades(this.GetForexTradesForm.accountId)
          .then(response => {
            console.log('GetForexTrades API response received:', response);          
            if (response) {
              this.$message.success('查询外汇交易记录成功！');
              // 处理查询结果
              // 例如，将结果存储在一个变量中以供显示
              this.forexTrades = response; // 存储交易记录数据

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
      });
    },handleGetForexTradeDetails(){
      // Validate form first
      this.$refs.GetForexTradeDetailsFormRef.validate((valid) => {
        if (!valid) {
          this.$message.error('请填写完整的交易信息！');
          return;
        }
        
        console.log("GetForexTradeDetailsForm:",
        "Trade ID:", this.GetForexTradeDetailsForm.TradeId
        );
        
        const payload = {
          TradeId: this.GetForexTradeDetailsForm.TradeId
        };
        console.log('Payload for GetForexTradeDetails:', payload);
        
        // 调用查询外汇交易详情的API
        getForexTradeDetails(this.GetForexTradeDetailsForm.TradeId)        .then(response => {
          console.log('GetForexTradeDetails API response received:', response);
          if (response) {
            this.$message.success('查询外汇交易详情成功！');
            // 存储查询结果并显示
            this.tradeDetailsResult = response;
            this.tradeDetailsResultVisible = true;

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

            this.GetForexTradeDetailsVisible = false; // 关闭查询对话框

          } else {
            console.log('GetForexTradeDetails API response is empty or undefined.');
            this.$message.error('查询外汇交易详情失败');
            // 打印response以便调试
            console.error('GetForexTradeDetails API response:', response);
          }
        })        .catch(error => {
          console.log('GetForexTradeDetails API error:', error);
          console.error('GetForexTradeDetails error:', error);
          this.$message.error('查询外汇交易详情失败，请稍后再试！');
        });
      });
    },// 格式化汇率显示
    formatRate(rate) {
      if (!rate) return '-';
      return parseFloat(rate).toFixed(4);
    },

    // 格式化金额显示
    formatAmount(amount) {
      if (!amount) return '-';
      return parseFloat(amount).toFixed(2);
    },

    // 格式化日期时间显示
    formatDateTime(dateTime) {
      if (!dateTime) return '-';
      const date = new Date(dateTime);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    // 获取状态标签类型
    getStatusType(status) {
      switch (status) {
        case 'SUCCESS':
        case 'COMPLETED':
          return 'success';
        case 'PENDING':
          return 'warning';
        case 'FAILED':
        case 'CANCELLED':
          return 'danger';
        default:
          return 'info';
      }
    },

    // 快速交易
    quickTrade(rate) {
      this.selectedRate = rate;
      this.quickTradeDialogVisible = true;
      // 重置表单
      this.quickTradeForm = {
        amount: null,
        type: 'BUY'
      };
      if (this.$refs.quickTradeFormRef) {
        this.$refs.quickTradeFormRef.resetFields();
      }
    },

    // 处理快速交易
    handleQuickTrade() {
      if (!this.quickTradeForm.amount || !this.quickTradeForm.type) {
        this.$message.error('请填写完整的交易信息！');
        return;
      }

      const payload = {
        token: this.token, // 使用存储的token
        accountId: this.userId.toString(10), // 使用当前登录用户的ID
        currencyPair: this.selectedRate.currencyPair,
        amount: parseFloat(this.quickTradeForm.amount),
        type: this.quickTradeForm.type
      };

      console.log('Quick trade payload:', payload);

      // 调用外汇交易API
      tradeForex(payload)        .then(response => {
          console.log('Quick trade API response:', response);
          if (response) {
            this.$message.success(`交易成功！交易ID: ${response.tradeId}`);
            this.quickTradeDialogVisible = false;
            // 刷新汇率数据
            this.handleGetAllExchangeRates();
            // 刷新交易记录
            this.refreshForexTrades();
          } else {
            this.$message.error('交易失败');
          }
        })        .catch(error => {
          console.error('Quick trade error:', error);
          this.$message.error('交易失败，请稍后再试！');
        });
    },    // 刷新外汇交易记录
    refreshForexTrades() {
      getForexTrades(this.userId)
        .then(response => {
          if (response) {
            this.forexTrades = response;
            console.log('Forex trades refreshed:', response.length, 'records');
          }
        })
        .catch(error => {
          console.error('Failed to refresh forex trades:', error);
        });
    },    // 从查询结果开始快速交易
    startQuickTradeFromResult() {
      if (this.exchangeRateResult) {
        // 构造类似汇率表格数据的对象
        const rateData = {
          currencyPair: this.exchangeRateResult.currencyPair,
          currentRate: this.exchangeRateResult.rate
        };
        this.exchangeRateResultVisible = false;
        this.quickTrade(rateData);
      }
    },

    // 删除汇率记录
    removeExchangeRate(index) {
      this.$confirm('确定要删除这条汇率记录吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.exchangeRates.splice(index, 1);
        this.$message.success('汇率记录已删除');
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },    // 删除外汇交易记录
    removeForexTrade(index) {
      this.$confirm('确定要删除这条交易记录吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.forexTrades.splice(index, 1);
        this.$message.success('交易记录已删除');
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    // 删除当前查询的汇率结果
    removeCurrentExchangeRate() {
      this.$confirm('确定要删除这条汇率记录吗？删除后将从查询结果中移除。', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 如果汇率结果在汇率列表中，也要从列表中删除
        if (this.exchangeRateResult && this.exchangeRates.length > 0) {
          const index = this.exchangeRates.findIndex(rate => 
            rate.foreignExchangeId === this.exchangeRateResult.exchangeId ||
            rate.currencyPair === this.exchangeRateResult.currencyPair
          );
          if (index !== -1) {
            this.exchangeRates.splice(index, 1);
          }
        }
        // 清空当前查询结果并关闭对话框
        this.exchangeRateResult = null;
        this.exchangeRateResultVisible = false;
        this.$message.success('汇率记录已删除');
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    // 删除当前查询的交易详情结果
    removeCurrentTradeDetails() {
      this.$confirm('确定要删除这条交易记录吗？删除后将从交易记录中移除。', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 如果交易详情在交易记录列表中，也要从列表中删除
        if (this.tradeDetailsResult && this.forexTrades.length > 0) {
          const index = this.forexTrades.findIndex(trade => 
            trade.foreignExchangeTradeId === this.tradeDetailsResult.foreignExchangeTradeId
          );
          if (index !== -1) {
            this.forexTrades.splice(index, 1);
          }
        }
        // 清空当前查询结果并关闭对话框
        this.tradeDetailsResult = null;
        this.tradeDetailsResultVisible = false;
        this.$message.success('交易记录已删除');      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    // 清空汇率查询表单
    clearGetExchangeRateForm() {
      this.GetExchangeRateForm.currencyPair = '';
      if (this.$refs.GetExchangeRateFormRef) {
        this.$refs.GetExchangeRateFormRef.resetFields();
      }
      this.$message.success('表单已清空');
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

.exchange-rates-section {
  margin-top: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.forex-trades-section {
  margin-top: 30px;
  padding: 20px;
  background-color: #f0f7ff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  text-align: left;
  color: #333;
  font-weight: 600;
}

.rates-table, .trades-table {
  width: 100%;
  margin-top: 15px;
}

.rate-value {
  font-weight: 600;
  color: #409EFF;
  font-size: 1.1rem;
}

.amount-value {
  font-weight: 600;
  color: #67C23A;
  font-size: 1.0rem;
}

.button-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.action-button {
  width: 100%;
  padding: 12px 20px;
  font-size: 14px;
  border-radius: 6px;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.exchange-rate-result {
  padding: 20px 0;
}

.exchange-rate-result .rate-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #409EFF;
}

.trade-details-result {
  padding: 20px 0;
}

.trade-details-result .rate-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #409EFF;
}

.trade-details-result .amount-value {
  font-size: 1.1rem;
  font-weight: bold;
  color: #67C23A;
}
</style>
