<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-body">
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
            <el-icon :size="48" :style="{ color: item.color }">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">最近线索</span>
          </template>
          <el-table :data="recentLeads" stripe style="width: 100%">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="company" label="公司" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="source" label="来源" />
            <el-table-column prop="createdAt" label="创建时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">待办事项</span>
          </template>
          <div class="todo-list">
            <div class="todo-item" v-for="(item, idx) in todoList" :key="idx">
              <el-tag :type="item.type" size="small">{{ item.tag }}</el-tag>
              <span class="todo-text">{{ item.text }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">商机阶段分布</span>
          </template>
          <div class="stage-bars">
            <div class="stage-bar-item" v-for="item in stageData" :key="item.name">
              <span class="stage-name">{{ item.name }}</span>
              <el-progress :percentage="item.percent" :color="item.color" :stroke-width="16" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">最近成单</span>
          </template>
          <el-table :data="recentDeals" stripe style="width: 100%">
            <el-table-column prop="title" label="成交项目" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
            </el-table-column>
            <el-table-column prop="dealDate" label="成交日期" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/customer'

const statCards = ref([
  { title: '客户总数', value: 0, icon: 'User', color: '#409EFF' },
  { title: '线索数量', value: 0, icon: 'Document', color: '#67C23A' },
  { title: '进行中商机', value: 0, icon: 'TrendCharts', color: '#E6A23C' },
  { title: '本月成单', value: 0, icon: 'Money', color: '#F56C6C' },
])

const recentLeads = ref<any[]>([])
const recentDeals = ref<any[]>([])
const todoList = ref([
  { tag: '紧急', text: '联系张总确认合同细节', type: 'danger' as const },
  { tag: '跟进', text: '回访李经理的产品试用反馈', type: 'warning' as const },
  { tag: '提醒', text: '王总下周三来访准备资料', type: 'info' as const },
  { tag: '任务', text: '整理Q1成单数据报表', type: 'success' as const },
  { tag: '跟进', text: '发送产品报价给赵经理', type: 'warning' as const },
])

const stageData = ref([
  { name: '初步接洽', percent: 0, color: '#409EFF' },
  { name: '需求确认', percent: 0, color: '#67C23A' },
  { name: '方案报价', percent: 0, color: '#E6A23C' },
  { name: '商务谈判', percent: 0, color: '#F56C6C' },
  { name: '赢单', percent: 0, color: '#9B59B6' },
])

onMounted(async () => {
  try {
    const res: any = await getDashboardStats()
    if (res.data) {
      statCards.value[0].value = res.data.customerCount || 0
      statCards.value[1].value = res.data.leadCount || 0
      statCards.value[2].value = res.data.opportunityCount || 0
      statCards.value[3].value = res.data.monthDealCount || 0
      recentLeads.value = res.data.recentLeads || []
      recentDeals.value = res.data.recentDeals || []
      if (res.data.stageStats) {
        const total = res.data.stageStats.reduce((s: number, item: any) => s + item.count, 0) || 1
        stageData.value.forEach((item, idx) => {
          const found = res.data.stageStats[idx]
          item.percent = found ? Math.round((found.count / total) * 100) : 0
        })
      }
    }
  } catch {
    // demo fallback
    statCards.value[0].value = 128
    statCards.value[1].value = 56
    statCards.value[2].value = 34
    statCards.value[3].value = 12
    stageData.value[0].percent = 30
    stageData.value[1].percent = 25
    stageData.value[2].percent = 20
    stageData.value[3].percent = 15
    stageData.value[4].percent = 10
  }
})
</script>

<style scoped>
.stat-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}
.stat-title {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}
.todo-text {
  font-size: 14px;
  color: #555;
}
.stage-bars {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.stage-name {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
</style>
