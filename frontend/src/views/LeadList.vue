<template>
  <div class="lead-list">
    <div class="top-bar">
      <div class="search-area">
        <el-input v-model="searchKey" placeholder="搜索姓名/公司/电话" clearable style="width: 260px;" @keyup.enter="loadData" />
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 140px; margin-left: 12px;" @change="loadData">
          <el-option label="新线索" value="new" />
          <el-option label="联系中" value="contacting" />
          <el-option label="已转化" value="converted" />
          <el-option label="已流失" value="lost" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 8px;">搜索</el-button>
      </div>
      <el-button type="primary" @click="openDialog()">新增线索</el-button>
    </div>

    <el-table :data="filteredLeads" stripe border style="width: 100%">
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="company" label="公司" width="160" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="source" label="来源" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="success" :disabled="row.status === 'converted'" @click="handleConvert(row)">转化</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="520px" destroy-on-close>
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="公司" prop="company">
          <el-input v-model="form.company" placeholder="请输入公司" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" placeholder="如：官网、展会、推荐" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getLeads, createLead, convertLead, type Lead } from '@/api/lead'

const statusMap: Record<string, { label: string; type: string }> = {
  new: { label: '新线索', type: 'info' },
  contacting: { label: '联系中', type: 'warning' },
  converted: { label: '已转化', type: 'success' },
  lost: { label: '已流失', type: 'danger' },
}

const leads = ref<Lead[]>([])
const searchKey = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = ref<Lead>({ name: '', company: '', phone: '', email: '', source: '', status: 'new', remark: '' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
}

const dialogTitle = computed(() => editingId.value ? '编辑线索' : '新增线索')

const filteredLeads = computed(() => {
  let list = leads.value
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    list = list.filter((l: Lead) =>
      l.name?.toLowerCase().includes(key) ||
      l.company?.toLowerCase().includes(key) ||
      l.phone?.includes(key)
    )
  }
  if (statusFilter.value) {
    list = list.filter((l: Lead) => l.status === statusFilter.value)
  }
  return list
})

const loadData = async () => {
  try {
    const res: any = await getLeads()
    leads.value = res.data || res || []
  } catch {
    leads.value = []
  }
}

const openDialog = (row?: Lead) => {
  if (row) {
    editingId.value = row.id || null
    form.value = { ...row }
  } else {
    editingId.value = null
    form.value = { name: '', company: '', phone: '', email: '', source: '', status: 'new', remark: '' }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    await createLead(form.value)
    ElMessage.success(editingId.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled by interceptor */ }
}

const handleConvert = async (row: Lead) => {
  try {
    await ElMessageBox.confirm('确定将该线索转化为客户？', '转化确认', { type: 'warning' })
    await convertLead(row.id!)
    ElMessage.success('转化成功')
    loadData()
  } catch { /* cancelled */ }
}

const handleDelete = async (row: Lead) => {
  try {
    await ElMessageBox.confirm('确定删除该线索？', '删除确认', { type: 'warning' })
    // No deleteLead API defined, using generic request
    const request = (await import('@/api/request')).default
    await request.delete(`/leads/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.lead-list { padding: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-area { display: flex; align-items: center; }
</style>
