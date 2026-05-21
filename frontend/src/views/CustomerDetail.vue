<template>
  <div class="customer-detail" v-loading="loading">
    <el-page-header @back="$router.push('/customers')" title="返回列表" content="客户详情" style="margin-bottom: 20px;" />

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600;">基本信息</span></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="姓名">{{ customer.name }}</el-descriptions-item>
            <el-descriptions-item label="公司">{{ customer.company }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ customer.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ customer.email }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ customer.address }}</el-descriptions-item>
            <el-descriptions-item label="等级">
              <el-tag size="small">{{ customer.level }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="来源">{{ customer.source }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ customer.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover" style="margin-bottom: 16px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: 600;">跟进记录</span>
              <el-button type="primary" size="small" @click="followUpDialogVisible = true">添加跟进</el-button>
            </div>
          </template>
          <el-timeline v-if="followUps.length">
            <el-timeline-item
              v-for="item in followUps"
              :key="item.id"
              :timestamp="item.createdAt"
              placement="top"
              :type="followUpTypeMap[item.type]?.tagType || 'primary'"
            >
              <el-card shadow="never" style="padding: 8px 12px;">
                <el-tag size="small" :type="followUpTypeMap[item.type]?.tagType || 'primary'" style="margin-right: 8px;">
                  {{ followUpTypeMap[item.type]?.label || item.type }}
                </el-tag>
                {{ item.content }}
                <div v-if="item.nextTime" style="font-size: 12px; color: #999; margin-top: 4px;">
                  下次跟进: {{ item.nextTime }}
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无跟进记录" />
        </el-card>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span style="font-weight: 600;">关联商机</span></template>
              <el-table :data="opportunities" stripe size="small">
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="amount" label="金额" width="100">
                  <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
                </el-table-column>
                <el-table-column prop="stage" label="阶段" width="80" />
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span style="font-weight: 600;">关联成单</span></template>
              <el-table :data="deals" stripe size="small">
                <el-table-column prop="title" label="项目" />
                <el-table-column prop="amount" label="金额" width="100">
                  <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
                </el-table-column>
                <el-table-column prop="dealDate" label="日期" width="100" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-dialog title="添加跟进记录" v-model="followUpDialogVisible" width="480px" destroy-on-close>
      <el-form :model="followUpForm" label-width="100px" :rules="followUpRules" ref="followUpFormRef">
        <el-form-item label="跟进类型" prop="type">
          <el-select v-model="followUpForm.type" style="width: 100%;">
            <el-option label="电话" value="phone" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
            <el-option label="会议" value="meeting" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="followUpForm.content" type="textarea" :rows="4" placeholder="请输入跟进内容" />
        </el-form-item>
        <el-form-item label="下次跟进日期">
          <el-date-picker v-model="followUpForm.nextTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followUpDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddFollowUp">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getCustomer, type Customer } from '@/api/customer'
import { getFollowUps, createFollowUp, type FollowUp } from '@/api/followUp'
import { getOpportunities } from '@/api/opportunity'
import { getDeals } from '@/api/deal'

const route = useRoute()
const customerId = Number(route.params.id)

const loading = ref(false)
const customer = ref<Customer>({} as Customer)
const followUps = ref<FollowUp[]>([])
const opportunities = ref<any[]>([])
const deals = ref<any[]>([])

const followUpDialogVisible = ref(false)
const followUpFormRef = ref<FormInstance>()
const followUpForm = ref<{ type: string; content: string; nextTime?: string }>({ type: 'phone', content: '' })
const followUpRules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

const followUpTypeMap: Record<string, { label: string; tagType: string }> = {
  phone: { label: '电话', tagType: 'primary' },
  email: { label: '邮件', tagType: 'success' },
  visit: { label: '拜访', tagType: 'warning' },
  meeting: { label: '会议', tagType: 'danger' },
}

const loadData = async () => {
  loading.value = true
  try {
    const [custRes, fuRes, oppRes, dealRes]: any[] = await Promise.all([
      getCustomer(customerId),
      getFollowUps(customerId),
      getOpportunities({ customerId }),
      getDeals({ customerId }),
    ])
    customer.value = custRes.data || custRes
    followUps.value = fuRes.data || fuRes || []
    opportunities.value = oppRes.data || oppRes || []
    deals.value = dealRes.data || dealRes || []
  } catch { /* handled */ }
  loading.value = false
}

const handleAddFollowUp = async () => {
  await followUpFormRef.value?.validate()
  try {
    await createFollowUp({ customerId, ...followUpForm.value } as FollowUp)
    ElMessage.success('跟进记录已添加')
    followUpDialogVisible.value = false
    followUpForm.value = { type: 'phone', content: '' }
    loadData()
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.customer-detail { padding: 0; }
</style>
