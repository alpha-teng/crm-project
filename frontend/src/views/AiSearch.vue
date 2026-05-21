<template>
  <div class="ai-search">
    <div class="search-home" v-if="!hasSearched">
      <h1 class="search-title">AI 智能查询</h1>
      <p class="search-desc">用自然语言提问，智能检索客户信息</p>
      <div class="search-box">
        <el-input
          v-model="question"
          placeholder="例如：最近有哪些客户在谈合作？"
          size="large"
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="search-results-view" v-else>
      <div class="search-box-top">
        <el-input v-model="question" placeholder="继续提问..." @keyup.enter="handleSearch" clearable>
          <template #append>
            <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
          </template>
        </el-input>
      </div>

      <div class="chat-area">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role === 'user' ? 'msg-user' : 'msg-ai']">
          <div class="msg-label">{{ msg.role === 'user' ? '你' : 'AI' }}</div>
          <div class="msg-content">
            <div v-if="msg.cards && msg.cards.length">
              <el-card v-for="(card, ci) in msg.cards" :key="ci" shadow="hover" style="margin-bottom: 8px;">
                <div v-if="card.name" style="font-weight: 600; font-size: 15px;">{{ card.name }}</div>
                <div v-if="card.company" style="color: #666;">公司: {{ card.company }}</div>
                <div v-if="card.phone" style="color: #666;">电话: {{ card.phone }}</div>
                <div v-if="card.email" style="color: #666;">邮箱: {{ card.email }}</div>
                <div v-if="card.summary" style="color: #999; margin-top: 6px; white-space: pre-wrap;">{{ card.summary }}</div>
                <div v-if="card.relevance" style="font-size: 12px; color: #409EFF; margin-top: 4px;">相关度: {{ (card.relevance * 100).toFixed(0) }}%</div>
              </el-card>
            </div>
            <div v-else style="white-space: pre-wrap;">{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="loading" class="chat-msg msg-ai">
          <div class="msg-label">AI</div>
          <div class="msg-content"><el-icon class="is-loading"><Loading /></el-icon> 思考中...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { aiQuery } from '@/api/knowledge'

interface ChatMsg {
  role: 'user' | 'ai'
  content: string
  cards?: any[]
}

const question = ref('')
const loading = ref(false)
const hasSearched = ref(false)
const messages = ref<ChatMsg[]>([])

const handleSearch = async () => {
  if (!question.value.trim()) return
  const q = question.value
  messages.value.push({ role: 'user', content: q })
  hasSearched.value = true
  question.value = ''
  loading.value = true
  try {
    const res: any = await aiQuery({ question: q })
    const data = res.data || res
    if (Array.isArray(data)) {
      messages.value.push({ role: 'ai', content: '', cards: data })
    } else if (data?.customers) {
      messages.value.push({ role: 'ai', content: '', cards: data.customers })
    } else if (data?.answer) {
      messages.value.push({ role: 'ai', content: data.answer, cards: data.customers })
    } else {
      messages.value.push({ role: 'ai', content: JSON.stringify(data, null, 2) })
    }
  } catch {
    messages.value.push({ role: 'ai', content: '查询失败，请重试' })
  }
  loading.value = false
}
</script>

<style scoped>
.ai-search { padding: 0; height: 100%; display: flex; flex-direction: column; }
.search-home { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.search-title { font-size: 36px; font-weight: 700; color: #409EFF; margin: 0; }
.search-desc { font-size: 16px; color: #999; margin: 12px 0 32px; }
.search-box { width: 600px; max-width: 90%; }
.search-results-view { display: flex; flex-direction: column; height: 100%; }
.search-box-top { margin-bottom: 16px; }
.chat-area { flex: 1; overflow-y: auto; padding: 8px 0; }
.chat-msg { margin-bottom: 16px; }
.msg-user { text-align: right; }
.msg-ai { text-align: left; }
.msg-label { font-size: 12px; color: #999; margin-bottom: 4px; }
.msg-user .msg-content { display: inline-block; background: #409EFF; color: #fff; padding: 8px 14px; border-radius: 12px; text-align: left; max-width: 70%; }
.msg-ai .msg-content { display: inline-block; background: #f5f7fa; padding: 8px 14px; border-radius: 12px; max-width: 80%; text-align: left; }
</style>
