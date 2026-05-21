<template>
  <div class="opportunity-board">
    <div class="top-bar">
      <h3 style="margin: 0;">商机看板</h3>
      <el-button type="primary" @click="openDialog()">新增商机</el-button>
    </div>

    <div class="board-container">
      <div class="board-column" v-for="stage in stages" :key="stage.key"
        @dragover.prevent @drop="handleDrop(stage.key, $event)">
        <div class="column-header" :style="{ background: stage.color }">
          {{ stage.label }}
          <el-badge :value="getStageItems(stage.key).length" :type="stage.badgeType" style="margin-left: 8px;" />
        </div>
        <div class="column-body">
          <div class="opp-card" v-for="item in getStageItems(stage.key)" :key="item.id"
            draggable="true" @dragstart="handleDragStart(item)">
            <div class="card-title">{{ item.title }}</div>
            <div class="card-info">
              <span>{{ item.customerName || '客户#' + item.customerId }}</span>
            </div>
            <div class="card-info">
              <span style="color: #F56C6C; font-weight: 600;">¥{{ item.amount?.toLocaleString() }}</span>
            </div>
            <div class="card-footer" v-if="item.remark">
              <span style="color: #999; font-size: 12px;">{{ item.remark }}</span>
            </div>
          </div>
          <el-empty v-if="!getStageItems(stage.key).length" description="暂无商机" :image-size="60" />
        </div>
      </div>
    </div>

    <el-dialog title="新增商机" v-model="dialogVisible" width="520px" destroy-on-close>
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="商机标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入商机标题" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="阶段" prop="stage">
          <el-select v-model="form.stage" style="width: 100%;">
            <el-option v-for="s in stages" :key="s.key" :label="s.label" :value="s.key" />
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getOpportunities, createOpportunity, updateOpportunityStage, type Opportunity } from '@/api/opportunity'

const stages = [
  { key: 'proposal', label: '初步接洽', color: '#409EFF', badgeType: 'primary' as const },
  { key: 'negotiation', label: '商务谈判', color: '#E6A23C', badgeType: 'warning' as const },
  { key: 'contract', label: '合同签订', color: '#67C23A', badgeType: 'success' as const },
  { key: 'won', label: '赢单', color: '#9B59B6', badgeType: '' as const },
  { key: 'lost', label: '输单', color: '#F56C6C', badgeType: 'danger' as const },
]

const opportunities = ref<Opportunity[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const draggedItem = ref<Opportunity | null>(null)
const form = ref<Opportunity>({ customerId: 0, title: '', amount: 0, stage: 'proposal', remark: '' })
const rules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  stage: [{ required: true, message: '请选择阶段', trigger: 'change' }],
}

const getStageItems = (stage: string) => opportunities.value.filter(o => o.stage === stage)

const loadData = async () => {
  try {
    const res: any = await getOpportunities()
    opportunities.value = res.data || res || []
  } catch {
    opportunities.value = []
  }
}

const handleDragStart = (item: Opportunity) => {
  draggedItem.value = item
}

const handleDrop = async (stage: string, _event: DragEvent) => {
  if (!draggedItem.value || draggedItem.value.stage === stage) return
  try {
    await updateOpportunityStage(draggedItem.value.id!, stage)
    draggedItem.value.stage = stage
    ElMessage.success('阶段已更新')
  } catch { /* handled */ }
  draggedItem.value = null
}

const openDialog = () => {
  form.value = { customerId: 0, title: '', amount: 0, stage: 'proposal', remark: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    await createOpportunity(form.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
.opportunity-board { padding: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.board-container { display: flex; gap: 12px; overflow-x: auto; min-height: 500px; }
.board-column { flex: 1; min-width: 200px; background: #f5f7fa; border-radius: 8px; display: flex; flex-direction: column; }
.column-header { padding: 12px 16px; color: #fff; font-weight: 600; border-radius: 8px 8px 0 0; font-size: 14px; display: flex; align-items: center; }
.column-body { padding: 8px; flex: 1; }
.opp-card { background: #fff; border-radius: 6px; padding: 12px; margin-bottom: 8px; cursor: grab; box-shadow: 0 1px 4px rgba(0,0,0,0.08); transition: box-shadow 0.2s; }
.opp-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.card-title { font-weight: 600; font-size: 14px; margin-bottom: 6px; color: #333; }
.card-info { font-size: 13px; color: #666; margin-bottom: 4px; }
.card-footer { margin-top: 6px; }
</style>
