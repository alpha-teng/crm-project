import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const Layout = () => import('@/components/Layout.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'leads', name: 'LeadList', component: () => import('@/views/LeadList.vue'), meta: { title: '线索管理' } },
      { path: 'customers', name: 'CustomerList', component: () => import('@/views/CustomerList.vue'), meta: { title: '客户管理' } },
      { path: 'customers/:id', name: 'CustomerDetail', component: () => import('@/views/CustomerDetail.vue'), meta: { title: '客户详情' } },
      { path: 'follow-ups', name: 'FollowUpList', component: () => import('@/views/FollowUpList.vue'), meta: { title: '跟进记录' } },
      { path: 'opportunities', name: 'OpportunityBoard', component: () => import('@/views/OpportunityBoard.vue'), meta: { title: '商机看板' } },
      { path: 'deals', name: 'DealList', component: () => import('@/views/DealList.vue'), meta: { title: '成单管理' } },
      { path: 'knowledge', name: 'KnowledgeBase', component: () => import('@/views/KnowledgeBase.vue'), meta: { title: '知识库' } },
      { path: 'ai-search', name: 'AiSearch', component: () => import('@/views/AiSearch.vue'), meta: { title: 'AI查询' } },
      { path: 'map', name: 'CustomerMap', component: () => import('@/views/CustomerMap.vue'), meta: { title: '客户地图' } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
