<template>
  <div class="add-record-container">
    <div class="form-card">
      <h2>{{ isEdit ? '编辑记录' : '添加记录' }}</h2>
      
      <div class="type-tabs">
        <button 
          :class="['tab-btn', form.type === 0 ? 'active' : '']"
          @click="form.type = 0"
        >
          支出
        </button>
        <button 
          :class="['tab-btn', form.type === 1 ? 'active' : '']"
          @click="form.type = 1"
        >
          收入
        </button>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>金额</label>
          <input 
            v-model.number="form.amount" 
            type="number" 
            step="0.01"
            placeholder="请输入金额" 
            required 
          />
        </div>

        <div class="form-group">
          <label>分类</label>
          <div class="category-grid">
            <div 
              v-for="cat in currentCategories" 
              :key="cat.id"
              :class="['category-item', form.categoryId === cat.id ? 'selected' : '']"
              @click="form.categoryId = cat.id"
            >
              <span class="icon">{{ cat.icon }}</span>
              <span class="name">{{ cat.name }}</span>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>日期</label>
          <input v-model="form.recordDate" type="date" required />
        </div>

        <div class="form-group">
          <label>备注</label>
          <textarea v-model="form.remark" placeholder="请输入备注（可选）" rows="3"></textarea>
        </div>

        <button type="submit" class="btn-submit">
          {{ isEdit ? '保存修改' : '添加记录' }}
        </button>
        <button v-if="isEdit" type="button" class="btn-cancel" @click="goBack">
          取消
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../utils/request'
import dayjs from '../utils/dayjs'

const router = useRouter()
const route = useRoute()

const user = computed(() => {
  const userData = localStorage.getItem('user')
  return userData ? JSON.parse(userData) : null
})

const categories = ref([])
const isEdit = ref(false)
const editId = ref(null)

const form = ref({
  type: 0,
  amount: '',
  categoryId: null,
  recordDate: dayjs().format('YYYY-MM-DD'),
  remark: ''
})

const currentCategories = computed(() => {
  return categories.value.filter(cat => cat.type === form.value.type)
})

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

const checkEditMode = () => {
  if (route.query.edit) {
    try {
      const editData = JSON.parse(route.query.edit)
      isEdit.value = true
      editId.value = editData.id
      form.value = {
        type: editData.type,
        amount: editData.amount,
        categoryId: editData.category_id,
        recordDate: editData.record_date,
        remark: editData.remark || ''
      }
    } catch (e) {
      console.error('解析编辑数据失败:', e)
    }
  }
}

const goBack = () => {
  router.push('/home/records')
}

const handleSubmit = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    alert('请输入有效金额')
    return
  }
  if (!form.value.categoryId) {
    alert('请选择分类')
    return
  }

  try {
    const data = {
      userId: user.value.id,
      type: form.value.type,
      amount: form.value.amount,
      categoryId: form.value.categoryId,
      recordDate: form.value.recordDate,
      remark: form.value.remark
    }

    if (isEdit.value) {
      data.id = editId.value
      await request.post('/record/update', data)
      alert('修改成功')
    } else {
      await request.post('/record/add', data)
      alert('添加成功')
      form.value = {
        type: 0,
        amount: '',
        categoryId: null,
        recordDate: dayjs().format('YYYY-MM-DD'),
        remark: ''
      }
    }
    goBack()
  } catch (error) {
    alert(error.message)
  }
}

onMounted(() => {
  loadCategories()
  checkEditMode()
})
</script>

<style scoped>
.add-record-container {
  max-width: 600px;
  margin: 0 auto;
}

.form-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-card h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.type-tabs {
  display: flex;
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #667eea;
  color: white;
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
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  border-color: #667eea;
}

.category-item.selected {
  border-color: #667eea;
  background: #f0f2ff;
}

.category-item .icon {
  font-size: 28px;
  margin-bottom: 4px;
}

.category-item .name {
  font-size: 12px;
  color: #666;
}

.btn-submit {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.btn-submit:hover {
  opacity: 0.9;
}

.btn-cancel {
  width: 100%;
  padding: 14px;
  margin-top: 12px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-cancel:hover {
  background: #e5e5e5;
}
</style>
