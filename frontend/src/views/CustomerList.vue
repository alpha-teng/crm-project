<template>
  <div class="customer-list">
    <div class="top-bar">
      <div class="search-area">
        <el-input v-model="searchKey" placeholder="搜索姓名/公司/电话" clearable style="width: 260px;" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData" style="margin-left: 8px;">搜索</el-button>
      </div>
      <el-button type="primary" @click="openDialog()">新增客户</el-button>
    </div>

    <el-table :data="filteredCustomers" stripe border style="width: 100%">
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="company" label="公司" width="150" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
      <el-table-column prop="level" label="等级" width="80">
        <template #default="{ row }">
          <el-tag size="small" :type="levelType(row.level)">{{ row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="source" label="来源" width="80" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="$router.push(`/customers/${row.id}`)">详情</el-button>
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="success" @click="handleAddKnowledge(row)">加入知识库</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="580px" destroy-on-close>
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="公司" prop="company">
          <el-input v-model="form.company" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number v-model="form.longitude" :controls="false" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number v-model="form.latitude" :controls="false" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="来源">
          <el-input v-model="form.source" placeholder="如：官网、展会" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="form.level" style="width: 100%;">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
            <el-option label="D" value="D" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getCustomers, createCustomer, updateCustomer, deleteCustomer, type Customer } from '@/api/customer'
import { addToKnowledge } from '@/api/knowledge'

const customers = ref<Customer[]>([])
const searchKey = ref('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = ref<Customer>({ name: '', company: '', phone: '', email: '', address: '', longitude: undefined, latitude: undefined, source: '', level: 'B', remark: '' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
}

const dialogTitle = computed(() => editingId.value ? '编辑客户' : '新增客户')

const levelType = (level: string) => {
  const map: Record<string, string> = { A: 'danger', B: 'warning', C: 'info', D: '' }
  return map[level] || ''
}

const filteredCustomers = computed(() => {
  if (!searchKey.value) return customers.value
  const key = searchKey.value.toLowerCase()
  return customers.value.filter((c: Customer) =>
    c.name?.toLowerCase().includes(key) ||
    c.company?.toLowerCase().includes(key) ||
    c.phone?.includes(key)
  )
})

const loadData = async () => {
  try {
    const res: any = await getCustomers()
    customers.value = res.data || res || []
  } catch {
    customers.value = []
  }
}

const openDialog = (row?: Customer) => {
  if (row) {
    editingId.value = row.id || null
    form.value = { ...row }
  } else {
    editingId.value = null
    form.value = { name: '', company: '', phone: '', email: '', address: '', longitude: undefined, latitude: undefined, source: '', level: 'B', remark: '' }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    if (editingId.value) {
      await updateCustomer(editingId.value, form.value)
    } else {
      await createCustomer(form.value)
    }
    ElMessage.success(editingId.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

const handleDelete = async (row: Customer) => {
  try {
    await ElMessageBox.confirm('确定删除该客户？', '删除确认', { type: 'warning' })
    await deleteCustomer(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

const handleAddKnowledge = async (row: Customer) => {
  try {
    const content = `客户姓名: ${row.name}\n公司: ${row.company}\n电话: ${row.phone}\n邮箱: ${row.email}\n地址: ${row.address}\n备注: ${row.remark}`
    await addToKnowledge({ customerId: row.id!, content })
    ElMessage.success('已加入知识库')
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.customer-list { padding: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.search-area { display: flex; align-items: center; }
</style>
