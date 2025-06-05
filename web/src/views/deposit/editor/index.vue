<template>
  <div class="container">
    <el-tabs v-model="activeTab">
      <!-- 存款管理 -->
      <el-tab-pane label="存款管理" name="deposit">
        <div class="tab-content">
          <!-- 创建存款区域 -->
          <div class="section">
            <h2>创建存款</h2>
            <el-form 
              ref="depositFormRef" 
              :model="depositForm" 
              :rules="depositRules" 
              inline 
              class="form-container"
            >
              <el-form-item label="账户ID" prop="accountId">
                <el-input 
                  v-model="depositForm.accountId" 
                  type="number" 
                  placeholder="请输入账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="存款类型" prop="type">
                <el-select v-model="depositForm.type" placeholder="请选择存款类型" class="form-input">
                  <el-option label="活期存款" value="DEMAND" />
                  <el-option label="定期存款" value="FIXED" />
                </el-select>
              </el-form-item>
              <el-form-item label="存款金额" prop="amount">
                <el-input 
                  v-model="depositForm.amount" 
                  type="number" 
                  placeholder="请输入存款金额"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="handleCreateDeposit" 
                  :loading="depositLoading"
                  class="action-button"
                >
                  创建存款
                </el-button>
                <el-button @click="resetDepositForm" class="action-button">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <!-- 查询存款记录区域 -->
          <div class="section">
            <h2>查询存款记录</h2>
            <el-form :model="queryDepositForm" inline class="form-container">
              <el-form-item label="账户ID">
                <el-input 
                  v-model="queryDepositForm.accountId" 
                  type="number" 
                  placeholder="请输入账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="handleQueryDeposits" 
                  :loading="queryDepositLoading"
                  class="action-button"
                >
                  查询记录
                </el-button>
              </el-form-item>
            </el-form>
            
            <el-table 
              :data="depositList" 
              style="width: 100%; margin-top: 20px;" 
              v-loading="tableLoading"
              empty-text="暂无数据"
              class="data-table"
            >
              <el-table-column prop="id" label="存款ID" width="80" />
              <el-table-column prop="accountId" label="账户ID" width="100" />
              <el-table-column prop="type" label="类型" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.type === 'FIXED' ? 'success' : 'info'">
                    {{ scope.row.type === 'FIXED' ? '定期' : '活期' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="120" />
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="maturityTime" label="到期时间" width="180" />
              <el-table-column prop="interestRate" label="利率" width="80" />
              <el-table-column prop="isWithdrawn" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.isWithdrawn ? 'danger' : 'success'">
                    {{ scope.row.isWithdrawn ? '已取款' : '未取款' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="withdrawTime" label="取款时间" width="180" />
              <el-table-column label="操作" width="240">
                <template #default="scope">
                  <el-button 
                    size="mini" 
                    @click="handleCheckWithdraw(scope.row.id)"
                    :disabled="scope.row.isWithdrawn"
                  >
                    检查取款资格
                  </el-button>
                  <el-button 
                    size="mini" 
                    type="success" 
                    @click="handleWithdraw(scope.row.id)" 
                    :disabled="scope.row.isWithdrawn"
                  >
                    取款
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 取款资格检查弹窗 -->
          <el-dialog title="取款资格检查" :visible.sync="withdrawCheckDialogVisible" width="500px" center>
            <div v-if="withdrawCheckResult" class="withdraw-check-content">
              <div class="check-item">
                <span class="label">可取款状态：</span>
                <el-tag :type="withdrawCheckResult.canWithdraw ? 'success' : 'danger'">
                  {{ withdrawCheckResult.canWithdraw ? '可以取款' : '不能取款' }}
                </el-tag>
              </div>
              <div class="check-item" v-if="withdrawCheckResult.reasons && withdrawCheckResult.reasons.length">
                <span class="label">不能取款原因：</span>
                <span class="reason">{{ withdrawCheckResult.reasons.join('，') }}</span>
              </div>
              <div class="check-item">
                <span class="label">当前利息：</span>
                <span class="amount">{{ withdrawCheckResult.interest }} 元</span>
              </div>
              <div class="check-item">
                <span class="label">可取总金额：</span>
                <span class="total-amount">{{ withdrawCheckResult.totalAmount }} 元</span>
              </div>
            </div>
            <span slot="footer" class="dialog-footer">
              <el-button @click="withdrawCheckDialogVisible = false">关闭</el-button>
            </span>
          </el-dialog>
        </div>
      </el-tab-pane>

      <!-- 转账管理 -->
      <el-tab-pane label="转账管理" name="transfer">
        <div class="tab-content">
          <!-- 发起转账区域 -->
          <div class="section">
            <h2>发起转账</h2>
            <el-form 
              ref="transferFormRef" 
              :model="transferForm" 
              :rules="transferRules" 
              inline 
              class="form-container"
            >
              <el-form-item label="转出账户ID" prop="fromAccountId">
                <el-input 
                  v-model="transferForm.fromAccountId" 
                  type="number" 
                  placeholder="请输入转出账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="转入账户ID" prop="toAccountId">
                <el-input 
                  v-model="transferForm.toAccountId" 
                  type="number" 
                  placeholder="请输入转入账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="转账金额" prop="amount">
                <el-input 
                  v-model="transferForm.amount" 
                  type="number" 
                  placeholder="请输入转账金额"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="币种">
                <el-input 
                  v-model="transferForm.currency" 
                  placeholder="CNY"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="备注">
                <el-input 
                  v-model="transferForm.description" 
                  placeholder="请输入转账备注"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="handleTransfer" 
                  :loading="transferLoading"
                  class="action-button"
                >
                  发起转账
                </el-button>
                <el-button @click="resetTransferForm" class="action-button">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <!-- 查询余额区域 -->
          <div class="section">
            <h2>查询账户余额</h2>
            <el-form :model="balanceForm" inline class="form-container">
              <el-form-item label="账户ID">
                <el-input 
                  v-model="balanceForm.accountId" 
                  type="number" 
                  placeholder="请输入账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item label="币种">
                <el-input 
                  v-model="balanceForm.currency" 
                  placeholder="CNY"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="handleQueryBalance" 
                  :loading="balanceLoading"
                  class="action-button"
                >
                  查询余额
                </el-button>
              </el-form-item>
            </el-form>
            
            <div v-if="balanceResult" class="balance-result">
              <el-alert 
                :title="`账户余额：${balanceResult.balance} ${balanceResult.currency}`" 
                type="success" 
                show-icon 
                :closable="false"
              />
            </div>
          </div>

          <el-divider />

          <!-- 查询交易记录区域 -->
          <div class="section">
            <h2>查询交易记录</h2>
            <el-form :model="recordForm" inline class="form-container">
              <el-form-item label="账户ID">
                <el-input 
                  v-model="recordForm.accountId" 
                  type="number" 
                  placeholder="请输入账户ID"
                  class="form-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="handleQueryRecords" 
                  :loading="recordLoading"
                  class="action-button"
                >
                  查询记录
                </el-button>
              </el-form-item>
            </el-form>
            
            <el-table 
              :data="recordList" 
              style="width: 100%; margin-top: 20px;" 
              v-loading="recordTableLoading"
              empty-text="暂无交易记录"
              class="data-table"
            >
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="transactionId" label="交易流水号" width="180" />
              <el-table-column prop="fromAccountId" label="转出账户" width="100" />
              <el-table-column prop="toAccountId" label="转入账户" width="100" />
              <el-table-column prop="amount" label="金额" width="100" />
              <el-table-column prop="currency" label="币种" width="80" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">
                    {{ scope.row.status === 'SUCCESS' ? '成功' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="description" label="备注" min-width="120" />
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column prop="completedAt" label="完成时间" width="180" />
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { 
  createDeposit, 
  queryDeposits, 
  checkWithdrawEligibility, 
  withdrawDeposit,
  transferMoney,
  queryBalance,
  queryTransactionRecords
} from '@/api/bank'

import { mapGetters } from 'vuex';

export default {
  name: 'BankManage',
  data() {
    return {
      activeTab: 'deposit',
      // Loading状态
      depositLoading: false,
      queryDepositLoading: false,
      tableLoading: false,
      transferLoading: false,
      balanceLoading: false,
      recordLoading: false,
      recordTableLoading: false,
      
      // 存款相关
      depositForm: { 
        accountId: '', 
        type: '', 
        amount: '' 
      },
      queryDepositForm: { 
        accountId: '' 
      },
      depositList: [],
      withdrawCheckDialogVisible: false,
      withdrawCheckResult: null,
      
      // 转账相关
      transferForm: { 
        fromAccountId: '', 
        toAccountId: '', 
        amount: '', 
        currency: 'CNY', 
        description: '' 
      },
      balanceForm: { 
        accountId: '', 
        currency: 'CNY' 
      },
      balanceResult: null,
      recordForm: { 
        accountId: '' 
      },
      recordList: [],
      
      // 表单验证规则
      depositRules: {
        accountId: [
          { required: true, message: '请输入账户ID', trigger: 'blur' },
          { type: 'string', pattern: /^\d+$/, message: '账户ID必须是数字', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择存款类型', trigger: 'change' }
        ],
        amount: [
          { required: true, message: '请输入存款金额', trigger: 'blur' },
          { type: 'string', pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的金额', trigger: 'blur' }
        ]
      },
      transferRules: {
        fromAccountId: [
          { required: true, message: '请输入转出账户ID', trigger: 'blur' },
          { type: 'string', pattern: /^\d+$/, message: '账户ID必须是数字', trigger: 'blur' }
        ],
        toAccountId: [
          { required: true, message: '请输入转入账户ID', trigger: 'blur' },
          { type: 'string', pattern: /^\d+$/, message: '账户ID必须是数字', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '请输入转账金额', trigger: 'blur' },
          { type: 'string', pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的金额', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['token','userId','userName','roles']), // 假设 userId 是从 Vuex 获取的
  },
  methods: {
    // 存款相关方法
    handleCreateDeposit() {
      console.log('handleCreateDeposit called. Form data:', this.depositForm);
      
      if (this.$refs.depositFormRef) {
        this.$refs.depositFormRef.validate((valid) => {
          if (valid) {
            this.submitCreateDeposit();
          } else {
            this.$message.error('请检查表单输入');
            return false;
          }
        });
      } else {
        // 手动验证
        if (!this.depositForm.accountId || !this.depositForm.type || !this.depositForm.amount) {
          this.$message.error('请填写所有必填字段');
          return;
        }
        this.submitCreateDeposit();
      }
    },    
    submitCreateDeposit() {
      this.depositLoading = true;
      
      const payload = {
        token:this.token,
        userId: parseInt(this.depositForm.accountId, 10),
        type: this.depositForm.type,
        amount: parseFloat(this.depositForm.amount)
      };
      
      console.log('Create deposit payload:', payload);
      
      createDeposit(payload).then(response => {
        console.log('Create deposit response:', response);
        
        if (response) {
          this.$message.success('创建存款成功');
          this.queryDepositForm.accountId = this.depositForm.accountId;
          this.handleQueryDeposits();
          this.resetDepositForm();
        } else {
          this.$message.error('创建存款失败');
        }
      }).catch((error) => {
        console.error("Create deposit error:", error);
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                          ? error.response.data.msg 
                          : '创建存款失败，请检查网络或联系管理员';
        this.$message.error(errorMsg);
      }).finally(() => {
        this.depositLoading = false;
      });
    },

    handleQueryDeposits() {
      console.log('handleQueryDeposits called. Query data:', this.queryDepositForm);
      
      if (!this.queryDepositForm.accountId) {
        this.$message.error('请输入账户ID');
        return;
      }
      
      this.queryDepositLoading = true;
      this.tableLoading = true;
      
      const payload = {
        token:this.token,
        accountId: this.queryDepositForm.accountId
      };
      
      queryDeposits(payload).then(response => {
        console.log('Query deposits response:', response);
        
        if (response) {
          this.depositList = response || [];
          if (this.depositList.length === 0) {
            this.$message.info('未找到存款记录');
          }
        } else {
          this.depositList = [];
          this.$message.error(response.msg || '查询失败');
        }
      }).catch((error) => {
        console.error("Query deposits error:", error);
        this.depositList = [];
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                          ? error.response.data.msg 
                          : '查询失败，请检查网络或联系管理员';
        this.$message.error(errorMsg);
      }).finally(() => {
        this.queryDepositLoading = false;
        this.tableLoading = false;
      });
    },

    handleCheckWithdraw(depositId) {
      console.log('handleCheckWithdraw called. DepositId:', depositId);
      
      const payload = { 
        depositId 
      };
      
      checkWithdrawEligibility(payload).then(response => {
        console.log('Check withdraw response:', response);
        
        if (response && (response.code === 200 || response.code === 0)) {
          this.withdrawCheckResult = response.data;
          this.withdrawCheckDialogVisible = true;
        } else {
          this.$message.error(response.msg || '检查取款资格失败');
        }
      }).catch((error) => {
        console.error("Check withdraw error:", error);
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                          ? error.response.data.msg 
                          : '检查失败，请检查网络或联系管理员';
        this.$message.error(errorMsg);
      });
    },

    handleWithdraw(depositId) {
      console.log('handleWithdraw called. DepositId:', depositId);
      
      this.$confirm('确定要取款吗？', '确认取款', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const payload = { 
          // token:this.token,
          depositId 
        };
        
        withdrawDeposit(payload).then(response => {
          console.log('Withdraw response:', response);
          
          if (response) {
            this.$message.success('取款成功');
            this.handleQueryDeposits();
          } else {
            this.$message.error('取款失败');
          }
        }).catch((error) => {
          console.error("Withdraw error:", error);
          const errorMsg = error.response && error.response.data && error.response.data.msg 
                            ? error.response.data.msg 
                            : '取款失败，请检查网络或联系管理员';
          this.$message.error(errorMsg);
        });
      }).catch(() => {
        this.$message.info('已取消取款');
      });
    },

    resetDepositForm() {
      this.depositForm = {
        accountId: '',
        type: '',
        amount: ''
      };
      if (this.$refs.depositFormRef) {
        this.$refs.depositFormRef.resetFields();
      }
    },

    // 转账相关方法
    handleTransfer() {
      console.log('handleTransfer called. Form data:', this.transferForm);
      
      if (this.$refs.transferFormRef) {
        this.$refs.transferFormRef.validate((valid) => {
          if (valid) {
            this.submitTransfer();
          } else {
            this.$message.error('请检查表单输入');
            return false;
          }
        });
      } else {
        // 手动验证
        if (!this.transferForm.fromAccountId || !this.transferForm.toAccountId || !this.transferForm.amount) {
          this.$message.error('请填写所有必填字段');
          return;
        }
        if (this.transferForm.fromAccountId === this.transferForm.toAccountId) {
          this.$message.error('转出账户和转入账户不能相同');
          return;
        }
        this.submitTransfer();
      }
    },

    submitTransfer() {
      this.transferLoading = true;
      
      const payload = {
        token:this.token,
        fromAccountId: parseInt(this.transferForm.fromAccountId, 10),
        toAccountId: parseInt(this.transferForm.toAccountId, 10),
        amount: parseFloat(this.transferForm.amount),
        currency: this.transferForm.currency || 'CNY',
        description: this.transferForm.description || ''
      };
      
      console.log('Transfer payload:', payload);
      
      transferMoney(payload).then(response => {
        console.log('Transfer response:', response);
        
        if (response) {
          this.$message.success(response || '转账成功');
          this.resetTransferForm();
        } else {
          this.$message.error(response || '转账失败');
        }
      }).catch((error) => {
        console.error("Transfer error:", error);
        // const errorMsg = error.response && error.response.data && error.response.data.msg 
        //                   ? error.response.data.msg 
        //                   : '转账失败，请检查网络或联系管理员';
        // this.$message.error(errorMsg);
      }).finally(() => {
        this.transferLoading = false;
      });
    },

    handleQueryBalance() {
      console.log('handleQueryBalance called. Form data:', this.balanceForm);
      
      if (!this.balanceForm.accountId) {
        this.$message.error('请输入账户ID');
        return;
      }
      
      this.balanceLoading = true;
      
      queryBalance(
        parseInt(this.balanceForm.accountId, 10), 
        this.balanceForm.currency || 'CNY'
      ).then(response => {
        console.log('Query balance response:', response);
        
        if (response && (response.code === 200 || response.code === 0)) {
          this.balanceResult = response.data;
        } else {
          this.balanceResult = null;
          this.$message.error(response.msg || '查询余额失败');
        }
      }).catch((error) => {
        console.error("Query balance error:", error);
        this.balanceResult = null;
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                          ? error.response.data.msg 
                          : '查询失败，请检查网络或联系管理员';
        this.$message.error(errorMsg);
      }).finally(() => {
        this.balanceLoading = false;
      });
    },

    handleQueryRecords() {
      console.log('handleQueryRecords called. Form data:', this.recordForm);
      
      if (!this.recordForm.accountId) {
        this.$message.error('请输入账户ID');
        return;
      }
      
      this.recordLoading = true;
      this.recordTableLoading = true;
      
      queryTransactionRecords(parseInt(this.recordForm.accountId, 10)).then(response => {
        console.log('Query records response:', response);
        
        if (response && (response.code === 200 || response.code === 0)) {
          this.recordList = response.data || [];
          if (this.recordList.length === 0) {
            this.$message.info('未找到交易记录');
          }
        } else {
          this.recordList = [];
          this.$message.error(response.msg || '查询交易记录失败');
        }
      }).catch((error) => {
        console.error("Query records error:", error);
        this.recordList = [];
        const errorMsg = error.response && error.response.data && error.response.data.msg 
                          ? error.response.data.msg 
                          : '查询失败，请检查网络或联系管理员';
        this.$message.error(errorMsg);
      }).finally(() => {
        this.recordLoading = false;
        this.recordTableLoading = false;
      });
    },

    resetTransferForm() {
      this.transferForm = {
        fromAccountId: '',
        toAccountId: '',
        amount: '',
        currency: 'CNY',
        description: ''
      };
      if (this.$refs.transferFormRef) {
        this.$refs.transferFormRef.resetFields();
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.bank-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.bank-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  border: 1px solid rgba(255, 255, 255, 0.18);

  .cover-image {
    margin-right: 30px;
    
    .cover-img {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      object-fit: cover;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
    }
  }

  .example {
    text-align: center;
    
    .title {
      margin: 5px 0;
      color: white;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
      
      &:first-child {
        font-size: 2.5em;
        font-weight: bold;
      }
      
      &:nth-child(2) {
        font-size: 1.8em;
        font-weight: 500;
      }
      
      &:nth-child(3) {
        font-size: 1.2em;
        font-weight: 300;
        opacity: 0.9;
      }
    }
  }
}

.bank-tabs {
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;

  ::v-deep .el-tabs__header {
    margin: 0;
    background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
    
    .el-tabs__nav-wrap {
      padding: 0 20px;
    }
    
    .el-tabs__item {
      color: white;
      font-weight: 500;
      font-size: 16px;
      height: 60px;
      line-height: 60px;
      
      &.is-active {
        color: white;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 8px 8px 0 0;
      }
      
      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.1);
      }
    }
    
    .el-tabs__active-bar {
      background: white;
      height: 3px;
    }
  }
  
  ::v-deep .el-tabs__content {
    padding: 0;
  }
}

.tab-content {
  padding: 30px;
  background: white;
}

.section {
  margin-bottom: 40px;
  
  h2 {
    color: #333;
    font-size: 1.5em;
    font-weight: 600;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f093fb;
    display: inline-block;
  }
}

.form-container {
  background: #f8f9ff;
  padding: 25px;
  border-radius: 12px;
  border: 1px solid #e8ecf4;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  
  .el-form-item {
    margin-bottom: 18px;
    
    ::v-deep .el-form-item__label {
      font-weight: 500;
      color: #333;
    }
  }
}

.form-input {
  width: 200px;
  
  ::v-deep .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;
    
    &:focus {
      border-color: #f093fb;
      box-shadow: 0 0 0 2px rgba(240, 147, 251, 0.2);
    }
  }
  
  ::v-deep .el-select .el-input__inner {
    border-radius: 8px;
  }
}

.action-button {
  border-radius: 25px;
  padding: 10px 25px;
  font-weight: 500;
  transition: all 0.3s ease;
  
  &.el-button--primary {
    background: linear-gradient(45deg, #f093fb 0%, #f5576c 100%);
    border: none;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(240, 147, 251, 0.4);
    }
  }
  
  &:not(.el-button--primary) {
    border: 1px solid #dcdfe6;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
    }
  }
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  
  ::v-deep .el-table__header {
    background: #f8f9ff;
    
    th {
      background: #f8f9ff;
      color: #333;
      font-weight: 600;
      border-bottom: 2px solid #e8ecf4;
    }
  }
  
  ::v-deep .el-table__body {
    tr:hover > td {
      background: #f0f9ff;
    }
  }
}

.balance-result {
  margin-top: 20px;
  
  ::v-deep .el-alert {
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
  }
}

.withdraw-check-content {
  .check-item {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    padding: 12px;
    background: #f8f9ff;
    border-radius: 8px;
    
    .label {
      font-weight: 600;
      color: #333;
      margin-right: 10px;
      min-width: 120px;
    }
    
    .reason {
      color: #e74c3c;
      font-weight: 500;
    }
    
    .amount {
      color: #27ae60;
      font-weight: 600;
    }
    
    .total-amount {
      color: #f39c12;
      font-weight: 600;
      font-size: 16px;
    }
  }
}

::v-deep .el-dialog {
  border-radius: 12px;
  
  .el-dialog__header {
    background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
    padding: 20px;
    
    .el-dialog__title {
      color: white;
      font-weight: 600;
    }
    
    .el-dialog__close {
      color: white;
      
      &:hover {
        color: #f0f0f0;
      }
    }
  }
  
  .el-dialog__body {
    padding: 25px;
  }
}

::v-deep .el-divider {
  margin: 30px 0;
  
  .el-divider__text {
    background: white;
    color: #999;
    font-weight: 500;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .bank-container {
    padding: 10px;
  }
  
  .bank-header {
    flex-direction: column;
    text-align: center;
    
    .cover-image {
      margin-right: 0;
      margin-bottom: 15px;
    }
    
    .example .title {
      &:first-child {
        font-size: 2em;
      }
      
      &:nth-child(2) {
        font-size: 1.5em;
      }
      
      &:nth-child(3) {
        font-size: 1em;
      }
    }
  }
  
  .tab-content {
    padding: 15px;
  }
  
  .form-container {
    padding: 15px;
  }
  
  .form-input {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .action-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>