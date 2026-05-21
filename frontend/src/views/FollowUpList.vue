<template>
  <div class="follow-up-list">
    <div class="top-bar">
      <div class="search-area">
        <el-input v-model="customerFilter" placeholder="按客户名称筛选" clearable style="width: 260px;" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData" style="margin-left: 8px;">搜索</el-button>
      </div>
      <el-button type="primary" @click="openDialog()">添加跟进</el-button>
    </div>

    <el-table :data="filteredFollowUps" stripe border style="width: 100%">
      <el-table-column prop="customerId" label="客户ID" width="80" />
      <el-table-column prop="customerName" label="客户名称" width="120" />
      <el-table-column prop="type" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="typeMap[row.type]?.tagType || ''" size="small">{{ typeMap[row.type]?.label || row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="nextTime" label="下次跟进" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
    </el-table>

    <el-dialog title="添加跟进记录" v-model="dialogVisible" width="520px" destroy-on-close>
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="跟进类型" prop="type">
          <el-select v-model="form.type" style="width: 100%;">
            <el-option label="电话" value="phone" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
            <el-option label="会议" value="meeting" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入跟进内容" />
        </el-form-item>
        <el-form-item label="下次跟进日期">
          <el-date-picker v-model="form.nextTime" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%;" />
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
import { createFollowUp, type FollowUp } from '@/api/followUp'
import request from '@/api/request'

const typeMap: Record<string, { label: string; tagType: string }> = {
  phone: { label: '电话', tagType: 'primary' },
  email: { label: '邮件', tagType: 'success' },
  visit: { label: '拜访', tagType: 'warning' },
  meeting: { label: '会议', tagType: 'danger' },
}

const followUps = ref<any[]>([])
const customerFilter = ref('')
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = ref<{ customerId: number; type: string; content: string; nextTime?: string }>({ customerId: 0, type: 'phone', content: '' })
const rules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

const filteredFollowUps = computed(() => {
  if (!customerFilter.value) return followUps.value
  const key = customerFilter.value.toLowerCase()
  return followUps.value.filter(f =>
    String(f.customerId).includes(key) ||
    (f.customerName || '').toLowerCase().includes(key)
  )
})

const loadData = async () => {
  try {
    const res: any = await request.get('/follow-ups')
    followUps.value = res.data || res || []
  } catch {
    followUps.value = []
  }
}

const openDialog = () => {
  form.value = { customerId: 0, type: 'phone', content: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    await createFollowUp(form.value as FollowUp)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.follow-up-list { padding: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-area { display: flex; align-items: center; }
</style>
