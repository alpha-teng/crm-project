<template>
  <div class="knowledge-base">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: 600;">知识库文档</span>
              <el-input v-model="searchQuery" placeholder="语义搜索..." style="width: 300px;" clearable @keyup.enter="handleSearch">
                <template #append>
                  <el-button @click="handleSearch" :loading="searching">搜索</el-button>
                </template>
              </el-input>
            </div>
          </template>

          <!-- Search results -->
          <div v-if="searchResults.length" style="margin-bottom: 20px;">
            <h4 style="margin: 0 0 12px; color: #409EFF;">搜索结果</h4>
            <el-card v-for="(item, idx) in searchResults" :key="idx" shadow="never" style="margin-bottom: 8px;">
              <div style="font-weight: 600; margin-bottom: 4px;">{{ item.title || `文档 #${idx + 1}` }}</div>
              <div style="font-size: 13px; color: #666; white-space: pre-wrap;">{{ item.content || item.text }}</div>
              <div v-if="item.score !== undefined" style="font-size: 12px; color: #999; margin-top: 4px;">相似度: {{ (item.score * 100).toFixed(1) }}%</div>
            </el-card>
          </div>

          <!-- Document list -->
          <el-table :data="documents" stripe style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="customerId" label="客户ID" width="80" />
            <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="加入时间" width="170" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600;">添加客户资料到知识库</span></template>
          <el-form :model="addForm" label-width="80px">
            <el-form-item label="客户ID">
              <el-input-number v-model="addForm.customerId" :min="1" style="width: 100%;" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="addForm.content" type="textarea" :rows="4" placeholder="输入要加入知识库的内容" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAddKnowledge" :loading="adding">加入知识库</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px;">
          <template #header><span style="font-weight: 600;">客户列表快捷添加</span></template>
          <div v-for="c in customers" :key="c.id" class="customer-quick-item">
            <span>{{ c.name }} - {{ c.company }}</span>
            <el-button size="small" type="success" @click="quickAdd(c)">加入知识库</el-button>
          </div>
          <el-empty v-if="!customers.length" description="暂无客户" :image-size="50" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCustomers, type Customer } from '@/api/customer'
import { addToKnowledge, searchKnowledge } from '@/api/knowledge'
import request from '@/api/request'

const documents = ref<any[]>([])
const customers = ref<Customer[]>([])
const searchQuery = ref('')
const searchResults = ref<any[]>([])
const searching = ref(false)
const adding = ref(false)
const addForm = ref({ customerId: 0, content: '' })

const loadData = async () => {
  try {
    const [docRes, custRes]: any[] = await Promise.all([
      request.get('/knowledge'),
      getCustomers(),
    ])
    documents.value = docRes.data || docRes || []
    customers.value = custRes.data || custRes || []
  } catch {
    // handled
  }
}

const handleSearch = async () => {
  if (!searchQuery.value.trim()) return
  searching.value = true
  try {
    const res: any = await searchKnowledge({ query: searchQuery.value, topK: 5 })
    searchResults.value = res.data || res || []
  } catch {
    searchResults.value = []
  }
  searching.value = false
}

const handleAddKnowledge = async () => {
  if (!addForm.value.customerId || !addForm.value.content.trim()) {
    ElMessage.warning('请填写客户ID和内容')
    return
  }
  adding.value = true
  try {
    await addToKnowledge(addForm.value)
    ElMessage.success('已加入知识库')
    addForm.value = { customerId: 0, content: '' }
    loadData()
  } catch { /* handled */ }
  adding.value = false
}

const quickAdd = async (c: Customer) => {
  try {
    const content = `客户姓名: ${c.name}\n公司: ${c.company}\n电话: ${c.phone}\n邮箱: ${c.email}\n地址: ${c.address}\n备注: ${c.remark}`
    await addToKnowledge({ customerId: c.id!, content })
    ElMessage.success(`${c.name} 已加入知识库`)
    loadData()
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.knowledge-base { padding: 0; }
.customer-quick-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
.customer-quick-item:last-child { border-bottom: none; }
</style>
