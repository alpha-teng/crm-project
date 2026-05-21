<template>
  <div class="customer-map">
    <div id="map-container" style="width: 100%; height: calc(100vh - 120px);"></div>
    <el-button type="primary" class="add-customer-btn" @click="startAddCustomer">添加客户</el-button>

    <el-dialog title="添加客户" v-model="addDialogVisible" width="480px" destroy-on-close>
      <el-form :model="addForm" label-width="80px" :rules="addRules" ref="addFormRef">
        <el-form-item label="经度">
          <el-input-number v-model="addForm.longitude" :controls="false" :precision="6" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="addForm.latitude" :controls="false" :precision="6" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="公司" prop="company">
          <el-input v-model="addForm.company" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="addForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="addForm.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="addForm.address" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddCustomer">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getCustomers, createCustomer, type Customer } from '@/api/customer'

declare const AMap: any

let map: any = null
let markers: any[] = []
let clickListener: any = null
const addDialogVisible = ref(false)
const addingCustomer = ref(false)
const addFormRef = ref<FormInstance>()
const addForm = ref<Customer>({ name: '', company: '', phone: '', email: '', address: '', longitude: 0, latitude: 0, source: '', level: 'B', remark: '' })
const addRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  company: [{ required: true, message: '请输入公司', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
}

const initMap = () => {
  if (typeof AMap === 'undefined') {
    ElMessage.error('高德地图JS API未加载')
    return
  }
  map = new AMap.Map('map-container', {
    zoom: 11,
    center: [116.397428, 39.90923],
  })
}

const loadMarkers = async () => {
  try {
    const res: any = await getCustomers()
    const list: Customer[] = res.data || res || []
    clearMarkers()
    list.forEach(c => {
      if (c.longitude && c.latitude) {
        const marker = new AMap.Marker({
          position: [c.longitude, c.latitude],
          title: c.name,
        })
        const infoWindow = new AMap.InfoWindow({
          content: `<div style="padding:8px;"><strong>${c.name}</strong><br/>公司: ${c.company}<br/>电话: ${c.phone}<br/>地址: ${c.address || ''}</div>`,
          offset: new AMap.Pixel(0, -30),
        })
        marker.on('click', () => {
          infoWindow.open(map, marker.getPosition())
        })
        markers.push(marker)
        map.add(marker)
      }
    })
    // auto fit bounds
    if (markers.length) {
      map.setFitView(markers)
    }
  } catch { /* handled */ }
}

const clearMarkers = () => {
  if (map && markers.length) {
    map.remove(markers)
  }
  markers = []
}

const startAddCustomer = () => {
  ElMessage.info('请在地图上点击选择位置')
  clickListener = map.on('click', (e: any) => {
    addForm.value.longitude = parseFloat(e.lnglat.getLng().toFixed(6))
    addForm.value.latitude = parseFloat(e.lnglat.getLat().toFixed(6))
    addDialogVisible.value = true
    // remove listener after first click
    if (clickListener) {
      map.off('click', clickListener)
      clickListener = null
    }
  })
}

const handleAddCustomer = async () => {
  await addFormRef.value?.validate()
  addingCustomer.value = true
  try {
    await createCustomer(addForm.value)
    ElMessage.success('客户创建成功')
    addDialogVisible.value = false
    addForm.value = { name: '', company: '', phone: '', email: '', address: '', longitude: 0, latitude: 0, source: '', level: 'B', remark: '' }
    loadMarkers()
  } catch { /* handled */ }
  addingCustomer.value = false
}

onMounted(() => {
  initMap()
  loadMarkers()
})

onBeforeUnmount(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.customer-map { position: relative; padding: 0; }
.add-customer-btn { position: absolute; top: 16px; right: 16px; z-index: 999; }
</style>
