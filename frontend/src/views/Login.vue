<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="32"><DataBoard /></el-icon>
        <h2>CRM系统</h2>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        @keyup.enter="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item v-if="isRegister" prop="realName">
          <el-input
            v-model="formData.realName"
            placeholder="真实姓名"
            size="large"
            prefix-icon="UserFilled"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <el-button link type="primary" @click="isRegister = !isRegister">
          {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
        </el-button>
      </div>
      
      <div v-if="!isRegister" class="login-tip">
        <el-text type="info" size="small">默认管理员: admin / Afa123456@</el-text>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { login, register, getMe } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

const formData = reactive({
  username: '',
  password: '',
  realName: '',
})

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' },
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
  ],
}

const handleSubmit = async () => {
  const form = formRef.value
  if (!form) return
  
  await form.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      let res
      if (isRegister.value) {
        res = await register({
          username: formData.username,
          password: formData.password,
          realName: formData.realName,
        })
        ElMessage.success('注册成功，请登录')
        isRegister.value = false
        formData.username = ''
        formData.password = ''
        formData.realName = ''
      } else {
        res = await login({
          username: formData.username,
          password: formData.password,
        })
        
        const { token, user } = res.data || res
        localStorage.setItem('crm_token', token)
        localStorage.setItem('crm_user', JSON.stringify(user))
        
        ElMessage.success('登录成功')
        
        const redirect = (route.query.redirect as string) || '/'
        router.push(redirect)
      }
    } catch (error: any) {
      const msg = error.response?.data?.message || error.message || '操作失败'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 32px;
  color: #409EFF;
}

.login-header h2 {
  margin: 0;
  font-size: 24px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

.login-tip {
  text-align: center;
  margin-top: 12px;
}
</style>
