<template>
  <div class="budget-container">
    <div class="section-header">
      <h3 class="section-title">月度预算</h3>
      <input v-model="currentMonth" type="month" @change="loadData" class="month-select" />
      <button class="btn-add" @click="showAddModal = true">
        + 设置预算
      </button>
    </div>

    <div v-if="budgets.length === 0" class="empty">
      暂无预算设置，点击上方按钮添加
    </div>

    <div v-else class="budget-list">
      <div 
        v-for="budget in budgets" 
        :key="budget.id"
        :class="['budget-item', isOverBudget(budget) ? 'over-budget' : '']"
      >
        <div class="budget-header">
          <div class="budget-left">
            <span class="category-icon">{{ budget.category_icon || '📝' }}</span>
            <span class="category-name">{{ budget.category_name }}</span>
          </div>
          <div class="budget-right">
            <span v-if="isOverBudget(budget)" class="over-warning">
              ⚠️ 已超支
            </span>
            <button class="btn-action" @click="editBudget(budget)">编辑</button>
            <button class="btn-action danger" @click="deleteBudget(budget)">删除</button>
          </div>
        </div>

        <div class="budget-stats">
          <div class="stat-item">
            <span class="stat-label">预算金额</span>
            <span class="stat-value">¥{{ budget.budget_amount }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已使用</span>
            <span :class="['stat-value', isOverBudget(budget) ? 'over' : '']">
              ¥{{ budget.used_amount || 0 }}
            </span>
          </div>
          <div class="stat-item">
            <span class="stat-label">剩余</span>
            <span :class="['stat-value', getRemaining(budget) < 0 ? 'over' : '']">
              ¥{{ getRemaining(budget) }}
            </span>
          </div>
        </div>

        <div class="progress-container">
          <div class="progress-bar">
            <div 
              :class="['progress-fill', isOverBudget(budget) ? 'over' : '']"
              :style="{ width: getProgress(budget) + '%' }"
            ></div>
          </div>
          <div class="progress-text">
            {{ getProgress(budget) }}%
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ showAddModal ? '设置预算' : '编辑预算' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div v-if="showAddModal" class="form-group">
            <label>选择分类</label>
            <select v-model="form.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="cat in expenseCategories" :key="cat.id" :value="cat.id">
                {{ cat.icon }} {{ cat.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>预算金额</label>
            <input v-model.number="form.budgetAmount" type="number" step="0.01" placeholder="请输入预算金额" required />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeModal">取消</button>
            <button type="submit" class="btn-submit">确定</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../utils/request'
import dayjs from '../utils/dayjs'

const user = computed(() => {
  const userData = localStorage.getItem('user')
  return userData ? JSON.parse(userData) : null
})

const currentMonth = ref(dayjs().format('YYYY-MM'))
const budgets = ref([])
const expenseCategories = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const editId = ref(null)

const form = ref({
  categoryId: '',
  budgetAmount: ''
})

const loadCategories = async () => {
  if (!user.value) return
  try {
    const res = await request.get('/category/list', {
      params: { 
        userId: user.value.id,
        type: 0
      }
    })
    expenseCategories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadBudgets = async () => {
  if (!user.value) return
  try {
    const [year, month] = currentMonth.value.split('-').map(Number)
    const res = await request.get('/budget/list', {
      params: {
        userId: user.value.id,
        year,
        month
      }
    })
    budgets.value = res.data || []
  } catch (error) {
    console.error('加载预算失败:', error)
  }
}

const loadData = () => {
  loadBudgets()
}

const isOverBudget = (budget) => {
  const used = Number(budget.used_amount) || 0
  const budgetAmount = Number(budget.budget_amount) || 0
  return used > budgetAmount
}

const getRemaining = (budget) => {
  const budgetAmount = Number(budget.budget_amount) || 0
  const used = Number(budget.used_amount) || 0
  return (budgetAmount - used).toFixed(2)
}

const getProgress = (budget) => {
  const budgetAmount = Number(budget.budget_amount) || 0
  const used = Number(budget.used_amount) || 0
  if (budgetAmount === 0) return 0
  return Math.min(100, Math.round((used / budgetAmount) * 100))
}

const editBudget = (budget) => {
  editId.value = budget.id
  form.value = {
    categoryId: budget.category_id,
    budgetAmount: budget.budget_amount
  }
  showEditModal.value = true
}

const deleteBudget = async (budget) => {
  if (!confirm('确定要删除这个预算吗？')) return
  try {
    await request.post('/budget/delete', {
      userId: user.value.id,
      id: budget.id
    })
    loadData()
  } catch (error) {
    alert(error.message)
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editId.value = null
  form.value = {
    categoryId: '',
    budgetAmount: ''
  }
}

const handleSubmit = async () => {
  if (!form.value.budgetAmount || form.value.budgetAmount <= 0) {
    alert('请输入有效预算金额')
    return
  }

  if (showAddModal.value && !form.value.categoryId) {
    alert('请选择分类')
    return
  }

  try {
    const [year, month] = currentMonth.value.split('-').map(Number)
    if (showAddModal.value) {
      await request.post('/budget/set', {
        userId: user.value.id,
        categoryId: form.value.categoryId,
        year,
        month,
        budgetAmount: form.value.budgetAmount
      })
    } else {
      await request.post('/budget/update', {
        userId: user.value.id,
        id: editId.value,
        budgetAmount: form.value.budgetAmount
      })
    }
    closeModal()
    loadData()
  } catch (error) {
    alert(error.message)
  }
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.budget-container {
  max-width: 700px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.section-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.month-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.btn-add {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.btn-add:hover {
  opacity: 0.9;
}

.empty {
  padding: 60px 20px;
  text-align: center;
  color: #999;
  background: white;
  border-radius: 12px;
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.budget-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #667eea;
  transition: all 0.3s;
}

.budget-item.over-budget {
  border-left-color: #ff4d4f;
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.budget-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  font-size: 28px;
}

.category-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.budget-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.over-warning {
  font-size: 12px;
  color: #ff4d4f;
  background: #fff1f0;
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 8px;
}

.btn-action {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #e6f7ff;
  color: #1890ff;
}

.btn-action.danger {
  background: #fff1f0;
  color: #ff4d4f;
}

.budget-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.stat-value.over {
  color: #ff4d4f;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  transition: width 0.3s;
}

.progress-fill.over {
  background: linear-gradient(90deg, #ff4d4f 0%, #ff7875 100%);
}

.progress-text {
  font-size: 14px;
  font-weight: bold;
  color: #667eea;
  min-width: 50px;
  text-align: right;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
}

.modal-content h3 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.modal-actions button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-submit {
  background: #667eea;
  color: white;
}
</style>
