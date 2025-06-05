<template>
  <div class="container">
    <h2 class="title">贷款业务</h2>
    <div class="button-container">
        <!-- <el-button :loading="loading" type="primary" class="action-button" @click.native.prevent="handleLogin">登录</el-button> -->
        <!-- <el-button type="success" class="action-button" @click.native.prevent="showRegisterDialog">注册</el-button> -->
        <el-button type="primary" class="action-button" @click.native.prevent="showApplyLoanDialog">贷款申请</el-button>
        <!-- <el-button type="primary" class="action-button" @click.native.prevent="showApproveLoanDialog">贷款审批</el-button> -->
        <el-button type="primary" class="action-button" @click.native.prevent="showRepayLoanDialog">贷款还款</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetLoanApplicationsDialog">查询用户贷款申请</el-button>
        <el-button type="primary" class="action-button" @click.native.prevent="showGetLoansDialog">查询用户贷款记录</el-button>
        <!-- <el-button type="primary" class="action-button" @click.native.prevent="handleCheckOverdue">逾期检查（管理员功能）</el-button> -->

        <!-- <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button> -->
      </div>      <el-dialog title="贷款申请" :visible.sync="ApplyLoanVisible" width="50%" center>
        <el-form ref="ApplyLoanFormRef" :model="ApplyLoanForm" label-width="100px">
          
          <el-form-item label="申请人">
            <el-input :value="userName" disabled placeholder="当前登录用户" />
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
          </el-form-item>        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ApplyApproveVisible = false">取消</el-button>
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
        </div>      </el-dialog>

      <!-- 贷款申请结果展示对话框 -->
      <el-dialog title="贷款申请成功" :visible.sync="loanApplicationResultVisible" width="60%" center>
        <div v-if="loanApplicationResult" class="loan-result">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请记录ID">
              <el-tag type="primary">{{ loanApplicationResult.loanApplicationRecordId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="账户ID">
              <el-tag>{{ loanApplicationResult.accountId }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="贷款金额">
              <span class="amount-value">¥{{ formatAmount(loanApplicationResult.amountOfMoney) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="申请日期">
              <span>{{ loanApplicationResult.applicationDate }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="贷款期限">
              <el-tag type="info">{{ loanApplicationResult.term }} 个月</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="贷款用途">
              <span>{{ loanApplicationResult.purpose }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="信用评分">
              <el-tag :type="getCreditScoreType(loanApplicationResult.creditScore)">
                {{ loanApplicationResult.creditScore || '未提供' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请状态">
              <el-tag :type="getStatusType(loanApplicationResult.status)">
                {{ loanApplicationResult.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审批员ID">
              <span>{{ loanApplicationResult.loanExaminerId || '待分配' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              <span>{{ formatDateTime(loanApplicationResult.createdAt) }}</span>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="result-summary">
            <el-alert
              title="申请提交成功"
              type="success"
              :description="`您的贷款申请已成功提交，申请金额为 ¥${formatAmount(loanApplicationResult.amountOfMoney)}，期限为 ${loanApplicationResult.term} 个月。请耐心等待审批结果。`"
              show-icon
              :closable="false">
            </el-alert>
          </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="loanApplicationResultVisible = false">关闭</el-button>
          <el-button type="primary" @click="loanApplicationResultVisible = false">确定</el-button>        </div>
      </el-dialog>

      <!-- 查询用户贷款申请结果展示对话框 -->
      <el-dialog title="用户贷款申请查询结果" :visible.sync="loanApplicationsResultVisible" width="80%" center>
        <div v-if="loanApplicationsResult && loanApplicationsResult.length > 0" class="loan-applications-result">
          <div class="result-header">
            <el-alert
              :title="`共查询到 ${loanApplicationsResult.length} 条贷款申请记录`"
              type="info"
              show-icon
              :closable="false"
              style="margin-bottom: 20px;">
            </el-alert>
          </div>
          
          <div class="applications-list">
            <el-card 
              v-for="(application, index) in loanApplicationsResult" 
              :key="application.loanApplicationRecordId"
              class="application-card"
              shadow="hover"
              style="margin-bottom: 20px;">
              
              <div slot="header" class="card-header">
                <span class="card-title">申请记录 #{{ index + 1 }}</span>
                <el-tag :type="getStatusType(application.status)" size="medium">
                  {{ application.status }}
                </el-tag>
              </div>
              
              <el-descriptions :column="3" border>
                <el-descriptions-item label="申请记录ID" :span="2">
                  <el-tag type="primary" size="small">{{ application.loanApplicationRecordId }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="账户ID">
                  <el-tag>{{ application.accountId }}</el-tag>
                </el-descriptions-item>
                
                <el-descriptions-item label="申请金额">
                  <span class="amount-value" style="font-size: 16px; font-weight: bold; color: #E6A23C;">
                    {{ formatAmount(application.amountOfMoney) }}
                  </span>
                </el-descriptions-item>
                <el-descriptions-item label="贷款期限">
                  <el-tag type="info">{{ application.term }} 个月</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="申请日期">
                  <span>{{ application.applicationDate }}</span>
                </el-descriptions-item>
                
                <el-descriptions-item label="贷款用途" :span="2">
                  <span>{{ application.purpose }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="信用评分">
                  <el-tag :type="getCreditScoreType(application.creditScore)">
                    {{ application.creditScore || '未提供' }}
                  </el-tag>
                </el-descriptions-item>
                
                <el-descriptions-item label="审批员ID">
                  <span>{{ application.loanExaminerId || '待分配' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">
                  <span>{{ formatDateTime(application.createdAt) }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="更新时间">
                  <span>{{ formatDateTime(application.updatedAt) }}</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>
        </div>
        
        <div v-else class="no-applications">
          <el-empty description="未找到相关贷款申请记录">
            <el-button type="primary" @click="loanApplicationsResultVisible = false">返回</el-button>
          </el-empty>
        </div>
          <div slot="footer" class="dialog-footer">
          <el-button @click="loanApplicationsResultVisible = false">关闭</el-button>
          <el-button type="primary" @click="loanApplicationsResultVisible = false">确定</el-button>
        </div>
      </el-dialog>

      <!-- 查询用户贷款记录结果展示对话框 -->
      <el-dialog title="用户贷款记录查询结果" :visible.sync="loanRecordsResultVisible" width="80%" center>
        <div v-if="loanRecordsResult && loanRecordsResult.length > 0" class="loan-records-result">
          <div class="result-header">
            <el-alert
              :title="`找到 ${loanRecordsResult.length} 条贷款记录`"
              type="success"
              :closable="false"
              show-icon
              style="margin-bottom: 20px;">
            </el-alert>
          </div>
          
          <div class="records-list loan-record-cards">
            <el-card 
              v-for="(record, index) in loanRecordsResult" 
              :key="record.loanId"
              class="record-card loan-record-card"
              shadow="hover"
              style="margin-bottom: 20px;">
              
              <div slot="header" class="card-header">
                <span class="card-title">贷款记录 #{{ index + 1 }}</span>
                <div class="header-tags">
                  <el-tag :type="getStatusType(record.status)" size="medium">
                    {{ record.status }}
                  </el-tag>
                  <el-tag :type="record.isOverdue ? 'danger' : 'success'" size="medium" style="margin-left: 10px;">
                    {{ record.isOverdue ? '已逾期' : '正常' }}
                  </el-tag>
                </div>
              </div>
              
              <div class="card-content">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="贷款ID">
                    <el-tag type="primary" size="small">{{ record.loanId }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="申请记录ID">
                    <el-tag size="small">{{ record.loanApplicationRecordId }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="贷款金额">
                    <span class="amount-value" style="font-size: 16px; font-weight: bold; color: #E6A23C;">
                      {{ formatAmount(record.amountOfMoney) }}
                    </span>
                  </el-descriptions-item>
                  <el-descriptions-item label="利率">
                    <span style="font-weight: bold; color: #F56C6C;">
                      {{ (record.interestRate * 100).toFixed(2) }}%
                    </span>
                  </el-descriptions-item>
                  <el-descriptions-item label="还款日期">
                    <el-tag :type="record.isOverdue ? 'danger' : 'success'" size="medium">
                      {{ formatDate(record.repaymentDate) }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="逾期状态">
                    <el-tag :type="record.isOverdue ? 'danger' : 'success'">
                      {{ record.isOverdue ? '已逾期' : '正常' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="创建时间">
                    <span>{{ formatDateTime(record.createdAt) }}</span>
                  </el-descriptions-item>
                  <el-descriptions-item label="更新时间">
                    <span>{{ formatDateTime(record.updatedAt) }}</span>
                  </el-descriptions-item>
                </el-descriptions>
                
                <!-- 逾期警告 -->
                <div v-if="record.isOverdue" class="overdue-warning" style="margin-top: 15px;">
                  <el-alert
                    title="逾期警告"
                    description="此贷款已逾期，请尽快处理还款事宜"
                    type="error"
                    :closable="false"
                    show-icon>
                  </el-alert>
                </div>
              </div>
            </el-card>
          </div>
        </div>
        
        <div v-else class="no-records">
          <el-empty description="未找到相关贷款记录">
            <el-button type="primary" @click="loanRecordsResultVisible = false">返回</el-button>
          </el-empty>
        </div>
        
        <div slot="footer" class="dialog-footer">
          <el-button @click="loanRecordsResultVisible = false">关闭</el-button>
          <el-button type="primary" @click="loanRecordsResultVisible = false">确定</el-button>
        </div>
      </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex';

// 从/api/loan.js中导入API函数
import { applyLoan, approveLoan, repayLoan, getLoanApplications, getLoans, checkOverdue } from "@/api/loan";

export default {
  name: "CartPage",
  data() {
    return {
      loading: false,
      cartItems: [], // 存储购物车数据
      ApplyLoanVisible: false,
      ApplyApproveVisible: false,
      ApplyRepayVisible: false,
      ApplyGetLoanApplicationsVisible: false,      ApplyGetLoansVisible: false,
      ApplyCheckOverdueVisible: false,      loanApplicationResultVisible: false, // 贷款申请结果对话框显示状态
      loanApplicationResult: null, // 存储贷款申请结果数据
      loanApplicationsResultVisible: false, // 查询用户贷款申请结果对话框显示状态
      loanApplicationsResult: [], // 存储查询用户贷款申请结果数据
      loanRecordsResultVisible: false, // 查询用户贷款记录结果对话框显示状态
      loanRecordsResult: [], // 存储查询用户贷款记录结果数据
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
      },      ApplyLoanForm: {
        accountId: null, // 将在showApplyLoanDialog中设置为this.userId
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
    },    showApplyLoanDialog() {
      this.ApplyLoanVisible = true;
      // 清空上次的表单数据 (可选, 推荐)
      if (this.$refs.ApplyLoanFormRef) {
        this.$refs.ApplyLoanFormRef.resetFields(); // 如果设置了prop，可以这样重置
      }
      this.ApplyLoanForm = { // 或者手动重置
        accountId: this.userId, // 自动使用当前登录用户的ID
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
    },    handleApplyLoan(){
      // 1. 在这里添加日志，确认方法被调用以及表单数据是什么
      console.log('ApplyLoanForm:', 
      "Account ID (using userId):", this.userId,
      "Amount of Money:", this.ApplyLoanForm.amountOfMoney,
      "Application Date:", this.ApplyLoanForm.applicationDate,
      "Term:", this.ApplyLoanForm.term,
      "Purpose:", this.ApplyLoanForm.purpose,
      "Credit Score:", this.ApplyLoanForm.creditScore
      );
      // 2. 检查这个 IF 条件 - 确保所有必填字段都有值（账户ID使用this.userId）
      if (!this.userId || !this.ApplyLoanForm.amountOfMoney || !this.ApplyLoanForm.applicationDate || !this.ApplyLoanForm.term || !this.ApplyLoanForm.purpose) {
        console.log('Initial condition NOT met.');
        // 修改提示信息，移除账户ID要求
        this.$message.error('请填写完整的贷款信息：贷款金额、申请日期、贷款期限和贷款用途！');
      }else{
        console.log('Initial condition (Amount, Date, Term, Purpose) met.');
        const payload = {
          accountId: this.userId, // 使用当前登录用户的ID
          amountOfMoney: this.ApplyLoanForm.amountOfMoney,
          applicationDate: this.ApplyLoanForm.applicationDate,
          term: this.ApplyLoanForm.term,
          purpose: this.ApplyLoanForm.purpose,
          creditScore: this.ApplyLoanForm.creditScore
        };
        console.log('Payload for ApplyLoan:', payload);        // 3. 调用 ApplyLoan API
        applyLoan(payload)
          .then(response => {
            console.log('ApplyLoan API response received:', response);
            if (response) {
              this.$message.success('贷款申请成功！');
              console.log('ApplyLoan API response:', response);
              console.log('ApplyLoan API response data:', response);

              this.ApplyLoanVisible = false; // 关闭申请对话框
              
              // 存储申请结果并显示结果对话框
              this.loanApplicationResult = response;
              this.loanApplicationResultVisible = true;
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
        console.log('Payload for GetLoanApplications:', payload);        getLoanApplications(this.GetLoanApplicationsForm.accountId)
          .then(response => {
            console.log('GetLoanApplications API response received:', response);
            if (response) {
              this.$message.success('查询用户贷款申请成功！');
              console.log('GetLoanApplications API response:', response);
              console.log('GetLoanApplications API response data:', response.loanApplicationRecordId, response.accountId, response.amountOfMoney, response.applicationDate, response.loanExaminerId, response.creditScore, response.status, response.term, response.purpose, response.createdAt, response.updatedAt);
              
              this.ApplyGetLoanApplicationsVisible = false; // 关闭查询对话框
              
              // 存储查询结果并显示结果对话框
              this.loanApplicationsResult = Array.isArray(response) ? response : [response];
              this.loanApplicationsResultVisible = true;

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
        console.log('Payload for GetLoans:', payload);        getLoans(this.GetLoansForm.accountId)
          .then(response => {
            console.log('GetLoans API response received:', response);
            if (response) {
              this.$message.success('查询用户贷款记录成功！');
              console.log('GetLoans API response:', response);
              console.log('GetLoans API response data:', response.loanId, response.loanApplicationRecordId, response.amountOfMoney, response.repaymentDate, response.interestRate, response.isOverdue, response.status, response.createdAt, response.updatedAt);
              
              this.ApplyGetLoansVisible = false; // 关闭查询对话框
              
              // 存储查询结果并显示结果对话框
              this.loanRecordsResult = Array.isArray(response) ? response : [response];
              this.loanRecordsResultVisible = true;

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
            this.$message.error('逾期检查失败，请稍后再试！');          });
      // }
    },

    // 格式化金额显示
    formatAmount(amount) {
      if (!amount) return '0';
      return parseFloat(amount).toLocaleString('zh-CN', {
        style: 'currency',
        currency: 'CNY'
      });
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '未知';
      return new Date(dateTime).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    // 获取信用评分类型
    getCreditScoreType(score) {
      if (!score) return 'info';
      if (score >= 800) return 'success';
      if (score >= 700) return 'warning';
      return 'danger';
    },    // 获取状态类型
    getStatusType(status) {
      if (!status) return 'info';
      const statusLower = status.toLowerCase();
      if (statusLower === 'approved' || statusLower === '已批准') return 'success';
      if (statusLower === 'pending' || statusLower === '待审核') return 'warning';
      if (statusLower === 'rejected' || statusLower === '已拒绝') return 'danger';
      return 'info';
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '未知';
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
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

/* 贷款申请结果样式 */
.loan-result .amount-value {
  font-size: 16px;
  font-weight: bold;
  color: #E6A23C;
}

.loan-result .result-summary {
  margin-top: 20px;
}

/* 查询贷款申请结果样式 */
.loan-applications-result .result-header {
  margin-bottom: 20px;
}

.application-card {
  margin-bottom: 20px;
}

.application-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.application-card .card-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.no-applications {
  text-align: center;
  padding: 40px 0;
}

/* 贷款记录卡片样式 */
.loan-record-cards {
  max-height: 500px;
  overflow-y: auto;
}

.loan-record-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.loan-record-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.loan-record-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.loan-record-card .header-tags {
  display: flex;
  align-items: center;
}

.loan-record-card .card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.amount-value {
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

.overdue-warning {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid #f5f5f5;
}

.no-records {
  text-align: center;
  padding: 40px;
}
</style>
