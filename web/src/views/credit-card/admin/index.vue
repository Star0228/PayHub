<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>信用卡管理</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="handleCreateApplication"
        >申请新卡</el-button>
      </div>

      <!-- 信用卡列表 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的信用卡" name="cards">
          <el-table
            v-loading="loading"
            :data="creditCards"
            style="width: 100%"
          >
            <el-table-column
              prop="cardNumber"
              label="卡号"
              width="180"
            />
            <el-table-column
              prop="balance"
              label="余额"
              width="120"
            />
            <el-table-column
              prop="creditLimit"
              label="信用额度"
              width="120"
            />
            <el-table-column
              prop="status"
              label="状态"
              width="100"
            >
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="250"
            >
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  @click="handleViewTransactions(scope.row)"
                >交易记录</el-button>
                <el-button
                  size="mini"
                  type="primary"
                  @click="handleConsume(scope.row)"
                >消费</el-button>
                <el-button
                  size="mini"
                  type="success"
                  @click="handleRepay(scope.row)"
                >还款</el-button>
                <el-button
                  v-if="scope.row.status === 'NORMAL'"
                  size="mini"
                  type="danger"
                  @click="handleReportLost(scope.row)"
                >挂失</el-button>
                <el-button
                  v-if="scope.row.status === 'LOST'"
                  size="mini"
                  type="warning"
                  @click="handleCancelLost(scope.row)"
                >解挂</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="申请记录" name="applications">
          <el-table
            v-loading="loading"
            :data="applications"
            style="width: 100%"
          >
            <el-table-column
              prop="id"
              label="申请编号"
              width="120"
            />
            <el-table-column
              prop="applicationDate"
              label="申请时间"
              width="180"
            >
              <template slot-scope="scope">
                {{ formatDate(scope.row.applicationDate) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              width="120"
            >
              <template slot-scope="scope">
                <el-tag :type="getApplicationStatusType(scope.row.status)">
                  {{ getApplicationStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="reviewDate"
              label="审核时间"
              width="180"
            >
              <template slot-scope="scope">
                {{ scope.row.reviewDate ? formatDate(scope.row.reviewDate) : '-' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="reviewerId"
              label="审核人"
              width="120"
            >
              <template slot-scope="scope">
                {{ scope.row.reviewerId || '-' }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="审核管理" name="review">
          <div v-if="!isAdmin" class="admin-tip">
            <el-alert
              title="您不是管理员，无法访问此功能"
              type="warning"
              :closable="false"
              show-icon>
            </el-alert>
          </div>
          <el-table
            v-else
            v-loading="loading"
            :data="allApplications"
            style="width: 100%"
          >
            <el-table-column
              prop="id"
              label="申请编号"
              width="120"
            />
            <el-table-column
              prop="accountId"
              label="申请人ID"
              width="120"
            />
            <el-table-column
              prop="applicationDate"
              label="申请时间"
              width="180"
            >
              <template slot-scope="scope">
                {{ formatDate(scope.row.applicationDate) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              width="120"
            >
              <template slot-scope="scope">
                <el-tag :type="getApplicationStatusType(scope.row.status)">
                  {{ getApplicationStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="reviewDate"
              label="审核时间"
              width="180"
            >
              <template slot-scope="scope">
                {{ scope.row.reviewDate ? formatDate(scope.row.reviewDate) : '-' }}
              </template>
            </el-table-column>
            <el-table-column
              prop="reviewerId"
              label="审核人"
              width="120"
            >
              <template slot-scope="scope">
                {{ scope.row.reviewerId || '-' }}
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="200"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.status === 'PENDING'"
                  size="mini"
                  type="success"
                  @click="handleApprove(scope.row)"
                >通过</el-button>
                <el-button
                  v-if="scope.row.status === 'PENDING'"
                  size="mini"
                  type="danger"
                  @click="handleReject(scope.row)"
                >拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 消费对话框 -->
    <el-dialog title="信用卡消费" :visible.sync="consumeDialogVisible">
      <el-form :model="consumeForm" label-width="100px">
        <el-form-item label="消费金额">
          <el-input-number v-model="consumeForm.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="消费描述">
          <el-input v-model="consumeForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="consumeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitConsume">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 还款对话框 -->
    <el-dialog title="信用卡还款" :visible.sync="repayDialogVisible">
      <el-form :model="repayForm" label-width="100px">
        <el-form-item label="还款金额">
          <el-input-number v-model="repayForm.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="还款描述">
          <el-input v-model="repayForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="repayDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRepay">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 挂失对话框 -->
    <el-dialog title="信用卡挂失" :visible.sync="reportLostDialogVisible">
      <el-form :model="reportLostForm" label-width="100px">
        <el-form-item label="挂失原因">
          <el-input v-model="reportLostForm.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reportLostDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReportLost">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 交易记录对话框 -->
    <el-dialog title="交易记录" :visible.sync="transactionsDialogVisible" width="70%">
      <el-table :data="transactions" style="width: 100%">
        <el-table-column prop="createTime" label="交易时间" width="180" />
        <el-table-column prop="type" label="交易类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'CONSUME' ? 'danger' : 'success'">
              {{ scope.row.type === 'CONSUME' ? '消费' : '还款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMyCreditCards,
  getTransactions,
  consume,
  repay,
  reportLost,
  cancelLost,
  createApplication,
  getMyApplications,
  getAllApplications,
  updateApplicationStatus
} from '@/api/creditCard'
import { getToken } from '@/utils/auth'

export default {
  name: 'CreditCard',
  data() {
    return {
      activeTab: 'cards',
      loading: false,
      creditCards: [],
      applications: [],
      allApplications: [],
      transactions: [],
      currentCard: null,
      isAdmin: false,
      consumeDialogVisible: false,
      repayDialogVisible: false,
      reportLostDialogVisible: false,
      transactionsDialogVisible: false,
      consumeForm: {
        amount: 0,
        description: ''
      },
      repayForm: {
        amount: 0,
        description: ''
      },
      reportLostForm: {
        reason: ''
      }
    }
  },
  created() {
    this.checkAdminStatus()
    this.fetchData()
  },
  methods: {
    async checkAdminStatus() {
      try {
        const token = getToken()
        console.log('获取到的token:', token)
        if (token) {
          const base64Url = token.split('.')[1]
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
          const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
          }).join(''))
          const userInfo = JSON.parse(jsonPayload)
          console.log('解析后的用户信息:', userInfo)
          console.log('userFlag值:', userInfo.claims.userFlag)
          console.log('userFlag类型:', typeof userInfo.claims.userFlag)
          this.isAdmin = Number(userInfo.claims.userFlag) === 2
          console.log('设置isAdmin为:', this.isAdmin)
        } else {
          console.log('未找到token')
          this.isAdmin = false
        }
      } catch (error) {
        console.error('检查管理员状态失败:', error)
        this.isAdmin = false
      }
    },
    async fetchData() {
      this.loading = true
      try {
        console.log('开始获取数据...')
        console.log('当前isAdmin状态:', this.isAdmin)
        const [cardsRes, applicationsRes] = await Promise.all([
          getMyCreditCards(),
          getMyApplications()
        ])
        console.log('获取到的信用卡数据:', cardsRes)
        console.log('获取到的申请记录数据:', applicationsRes)
        this.creditCards = cardsRes
        this.applications = applicationsRes
        console.log('设置后的applications:', this.applications)

        // 如果是管理员，获取所有申请记录
        if (this.isAdmin) {
          console.log('开始获取所有申请记录...')
          try {
            const allApplicationsRes = await getAllApplications()
            console.log('获取到的所有申请记录:', allApplicationsRes)
            this.allApplications = allApplicationsRes
          } catch (error) {
            console.error('获取所有申请记录失败:', error)
            this.$message.error('获取所有申请记录失败：' + error.message)
          }
        } else {
          console.log('当前用户不是管理员，跳过获取所有申请记录')
        }
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败：' + error.message)
      }
      this.loading = false
    },
    getStatusType(status) {
      const map = {
        NORMAL: 'success',
        LOST: 'danger',
        FROZEN: 'warning'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        NORMAL: '正常',
        LOST: '已挂失',
        FROZEN: '已冻结'
      }
      return map[status] || status
    },
    getApplicationStatusType(status) {
      const map = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return map[status] || 'info'
    },
    getApplicationStatusText(status) {
      const map = {
        PENDING: '待审核',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
      }
      return map[status] || status
    },
    async handleCreateApplication() {
      try {
        await createApplication()
        this.$message.success('申请已提交')
        this.fetchData()
      } catch (error) {
        this.$message.error('申请失败：' + error.message)
      }
    },
    handleConsume(card) {
      console.log('点击消费按钮，当前卡片:', card)
      this.currentCard = card
      this.consumeForm = {
        amount: 0,
        description: ''
      }
      this.$nextTick(() => {
        this.consumeDialogVisible = true
        console.log('设置consumeDialogVisible为:', this.consumeDialogVisible)
      })
    },
    async submitConsume() {
      try {
        await consume(this.currentCard.id, this.consumeForm)
        this.$message.success('消费成功')
        this.consumeDialogVisible = false
        this.fetchData()
      } catch (error) {
        this.$message.error('消费失败：' + error.message)
      }
    },
    handleRepay(card) {
      this.currentCard = card
      this.repayForm = {
        amount: 0,
        description: ''
      }
      this.repayDialogVisible = true
    },
    async submitRepay() {
      try {
        await repay(this.currentCard.id, this.repayForm)
        this.$message.success('还款成功')
        this.repayDialogVisible = false
        this.fetchData()
      } catch (error) {
        this.$message.error('还款失败：' + error.message)
      }
    },
    handleReportLost(card) {
      this.currentCard = card
      this.reportLostForm.reason = ''
      this.reportLostDialogVisible = true
    },
    async submitReportLost() {
      try {
        await reportLost(this.currentCard.id, this.reportLostForm.reason)
        this.$message.success('挂失成功')
        this.reportLostDialogVisible = false
        this.fetchData()
      } catch (error) {
        this.$message.error('挂失失败：' + error.message)
      }
    },
    async handleCancelLost(card) {
      try {
        await cancelLost(card.id)
        this.$message.success('解挂成功')
        this.fetchData()
      } catch (error) {
        this.$message.error('解挂失败：' + error.message)
      }
    },
    async handleViewTransactions(card) {
      this.currentCard = card
      try {
        const res = await getTransactions(card.id)
        this.transactions = res.data
        this.transactionsDialogVisible = true
      } catch (error) {
        this.$message.error('获取交易记录失败：' + error.message)
      }
    },
    async handleApprove(application) {
      try {
        await updateApplicationStatus(application.id, 'APPROVED')
        this.$message.success('审核通过成功')
        this.fetchData()
      } catch (error) {
        this.$message.error('审核失败：' + error.message)
      }
    },
    async handleReject(application) {
      try {
        await updateApplicationStatus(application.id, 'REJECTED')
        this.$message.success('已拒绝申请')
        this.fetchData()
      } catch (error) {
        this.$message.error('操作失败：' + error.message)
      }
    },
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
</style> 