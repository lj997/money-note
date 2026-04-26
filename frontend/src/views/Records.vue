<template>
  <div class="records-container">
    <div class="filters">
      <div class="filter-item">
        <label>月份：</label>
        <input v-model="currentMonth" type="month" @change="loadData" />
      </div>
      <div class="filter-item">
        <label>分类：</label>
        <select v-model="selectedCategory" @change="loadData">
          <option value="">全部</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.icon }} {{ cat.name }}
          </option>
        </select>
      </div>
    </div>

    <div class="summary">
      <div class="summary-item income">
        <span class="label">收入</span>
        <span class="amount">+{{ summary.income || 0 }}</span>
      </div>
      <div class="summary-item expense">
        <span class="label">支出</span>
        <span class="amount">-{{ summary.expense || 0 }}</span>
      </div>
      <div class="summary-item balance">
        <span class="label">结余</span>
        <span class="amount">{{ (summary.income || 0) - (summary.expense || 0) }}</span>
      </div>
    </div>

    <div class="record-list">
      <div v-if="records.length === 0" class="empty">暂无记录</div>
      <div v-else>
        <div v-for="record in records" :key="record.id" class="record-item">
          <div class="record-left">
            <span class="category-icon">{{ record.category_icon || '📝' }}</span>
            <div class="record-info">
              <div class="category-name">{{ record.category_name }}</div>
              <div class="record-date">{{ record.record_date }}</div>
              <div v-if="record.remark" class="remark">{{ record.remark }}</div>
            </div>
          </div>
          <div class="record-right">
            <span :class="['amount', record.type === 1 ? 'income' : 'expense']">
              {{ record.type === 1 ? '+' : '-' }}{{ record.amount }}
            </span>
            <div class="actions">
              <button class="btn-edit" @click="editRecord(record)">编辑</button>
              <button class="btn-delete" @click="deleteRecord(record)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import dayjs from '../utils/dayjs'

const router = useRouter()

const user = computed(() => {
  const userData = localStorage.getItem('user')
  return userData ? JSON.parse(userData) : null
})

const currentMonth = ref(dayjs().format('YYYY-MM'))
const selectedCategory = ref('')
const records = ref([])
const categories = ref([])
const summary = ref({ income: 0, expense: 0 })

const loadCategories = async () => {
  if (!user.value) return
  try {
    const res = await request.get('/category/list', {
      params: { userId: user.value.id }
    })
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadRecords = async () => {
  if (!user.value) return
  try {
    const [year, month] = currentMonth.value.split('-').map(Number)
    const params = {
      userId: user.value.id,
      year,
      month
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    const res = await request.get('/record/list', { params })
    records.value = res.data
  } catch (error) {
    console.error('加载记录失败:', error)
  }
}

const loadSummary = async () => {
  if (!user.value) return
  try {
    const [year, month] = currentMonth.value.split('-').map(Number)
    const res = await request.get('/record/summary', {
      params: {
        userId: user.value.id,
        year,
        month
      }
    })
    summary.value = res.data
  } catch (error) {
    console.error('加载汇总失败:', error)
  }
}

const loadData = () => {
  loadRecords()
  loadSummary()
}

const editRecord = (record) => {
  router.push({
    path: '/home/add',
    query: { edit: JSON.stringify(record) }
  })
}

const deleteRecord = async (record) => {
  if (!confirm('确定要删除这条记录吗？')) return
  try {
    await request.post('/record/delete', {
      userId: user.value.id,
      id: record.id
    })
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
.records-container {
  max-width: 800px;
  margin: 0 auto;
}

.filters {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
}

.filter-item input,
.filter-item select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.summary {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.summary-item {
  flex: 1;
  min-width: 120px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item .label {
  font-size: 14px;
  color: #666;
}

.summary-item .amount {
  font-size: 20px;
  font-weight: bold;
}

.income .amount {
  color: #52c41a;
}

.expense .amount {
  color: #ff4d4f;
}

.balance .amount {
  color: #667eea;
}

.record-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #999;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.record-item:last-child {
  border-bottom: none;
}

.record-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  font-size: 28px;
}

.record-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-name {
  font-size: 16px;
  color: #333;
}

.record-date {
  font-size: 12px;
  color: #999;
}

.remark {
  font-size: 12px;
  color: #666;
}

.record-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.amount {
  font-size: 18px;
  font-weight: bold;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.btn-edit {
  background: #e6f7ff;
  color: #1890ff;
}

.btn-delete {
  background: #fff1f0;
  color: #ff4d4f;
}
</style>
