<template>
  <div class="deal-list">
    <div class="top-bar">
      <div class="stat-area">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">总成单金额</div>
          <div class="stat-value">¥{{ totalAmount.toLocaleString() }}</div>
        </el-card>
        <el-card shadow="hover" class="stat-card" style="margin-left: 16px;">
          <div class="stat-label">成单数量</div>
          <div class="stat-value">{{ deals.length }}</div>
        </el-card>
      </div>
      <el-button type="primary" @click="openDialog()">新建成单</el-button>
    </div>

    <el-table :data="deals" stripe border style="width: 100%">
      <el-table-column prop="title" label="成交项目" min-width="150" />
      <el-table-column prop="customerName" label="客户" width="120" />
      <el-table-column prop="amount" label="金额" width="130">
        <template #default="{ row }">
          <span style="color: #F56C6C; font-weight: 600;">¥{{ row.amount?.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="dealDate" label="成交日期" width="120" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
    </el-table>

    <el-dialog title="新建成单" v-model="dialogVisible" width="520px" destroy-on-close>
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="项目名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="成交日期" prop="dealDate">
          <el-date-picker v-model="form.dealDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getDeals, createDeal, type Deal } from '@/api/deal'

const deals = ref<Deal[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = ref<Deal>({ customerId: 0, title: '', amount: 0, dealDate: '', remark: '' })
const rules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  dealDate: [{ required: true, message: '请选择成交日期', trigger: 'change' }],
}

const totalAmount = computed(() => deals.value.reduce((sum, d) => sum + (d.amount || 0), 0))

const loadData = async () => {
  try {
    const res: any = await getDeals()
    deals.value = res.data || res || []
  } catch {
    deals.value = []
  }
}

const openDialog = () => {
  form.value = { customerId: 0, title: '', amount: 0, dealDate: '', remark: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    await createDeal(form.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.deal-list { padding: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.stat-area { display: flex; }
.stat-card { min-width: 160px; }
.stat-label { font-size: 13px; color: #999; }
.stat-value { font-size: 24px; font-weight: 700; color: #409EFF; margin-top: 4px; }
</style>
