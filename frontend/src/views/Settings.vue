<template>
  <div class="settings-container">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">AI 模型配置</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        label-width="180px"
        v-loading="loading"
        style="max-width: 640px;"
      >
        <el-form-item label="Embedding 模型 URL">
          <el-input v-model="form.embeddingUrl" placeholder="Embedding API URL" />
        </el-form-item>
        <el-form-item label="Embedding 模型名称">
          <el-input v-model="form.embeddingModel" placeholder="Embedding 模型名称" />
        </el-form-item>
        <el-form-item label="Chat 模型 URL">
          <el-input v-model="form.chatUrl" placeholder="Chat API URL" />
        </el-form-item>
        <el-form-item label="Chat 模型名称">
          <el-input v-model="form.chatModel" placeholder="Chat 模型名称" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="form.apiKey" type="password" show-password placeholder="API Key" />
        </el-form-item>
        <el-form-item label="Top K (K值)">
          <el-input-number v-model="form.topK" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="相似度阈值">
          <el-input-number v-model="form.similarityThreshold" :min="0" :max="1" :step="0.05" :precision="2" />
        </el-form-item>
        <el-form-item label="Embedding 维度">
          <el-input :model-value="form.embeddingDimension" disabled />
        </el-form-item>
        <el-form-item label="向量搜索最大返回数">
          <el-input-number v-model="form.maxSearchResults" :min="1" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettings, updateSettings } from '@/api/settings'

const loading = ref(false)
const saving = ref(false)

const form = reactive({
  embeddingUrl: '',
  embeddingModel: '',
  chatUrl: '',
  chatModel: '',
  apiKey: '',
  topK: 5,
  similarityThreshold: 0.6,
  embeddingDimension: 1024,
  maxSearchResults: 10,
})

const loadSettings = async () => {
  loading.value = true
  try {
    const data: any = await getSettings()
    Object.assign(form, data)
  } catch {
    // use defaults
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateSettings({ ...form })
    ElMessage.success('保存成功')
  } catch {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}

onMounted(loadSettings)
</script>

<style scoped>
.settings-container {
  padding: 0;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
