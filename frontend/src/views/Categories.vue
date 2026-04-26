<template>
  <div class="categories-container">
    <div class="type-tabs">
      <button 
        :class="['tab-btn', activeType === 0 ? 'active' : '']"
        @click="activeType = 0"
      >
        支出分类
      </button>
      <button 
        :class="['tab-btn', activeType === 1 ? 'active' : '']"
        @click="activeType = 1"
      >
        收入分类
      </button>
    </div>

    <div class="section">
      <h3 class="section-title">预设分类</h3>
      <div class="category-grid">
        <div 
          v-for="cat in defaultCategories" 
          :key="cat.id"
          class="category-item default"
        >
          <span class="icon">{{ cat.icon }}</span>
          <span class="name">{{ cat.name }}</span>
          <span class="tag">默认</span>
        </div>
      </div>
    </div>

    <div class="section">
      <div class="section-header">
        <h3 class="section-title">自定义分类</h3>
        <button class="btn-add" @click="showAddModal = true">
          + 添加分类
        </button>
      </div>
      <div v-if="customCategories.length === 0" class="empty">
        暂无自定义分类
      </div>
      <div v-else class="category-grid">
        <div 
          v-for="cat in customCategories" 
          :key="cat.id"
          class="category-item"
        >
          <span class="icon">{{ cat.icon }}</span>
          <span class="name">{{ cat.name }}</span>
          <div class="actions">
            <button class="btn-action" @click="editCategory(cat)">编辑</button>
            <button class="btn-action danger" @click="deleteCategory(cat)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ showAddModal ? '添加分类' : '编辑分类' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>分类名称</label>
            <input v-model="form.name" type="text" placeholder="请输入分类名称" required />
          </div>
          <div class="form-group">
            <label>图标</label>
            <div class="icon-selector">
              <span 
                v-for="(icon, index) in icons" 
                :key="index"
                :class="['icon-option', form.icon === icon ? 'selected' : '']"
                @click="form.icon = icon"
              >
                {{ icon }}
              </span>
            </div>
          </div>
          <div class="form-group">
            <label>排序</label>
            <input v-model.number="form.sort" type="number" placeholder="数字越小越靠前" />
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

const user = computed(() => {
  const userData = localStorage.getItem('user')
  return userData ? JSON.parse(userData) : null
})

const activeType = ref(0)
const categories = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const editId = ref(null)

const icons = ref(['🍽️', '🚗', '🛒', '🎮', '💊', '📚', '🏠', '💰', '🎁', '📈', '💼', '🎯', '🎨', '🎵', '✈️', '🏥', '🏫', '💄', '⚽', '📝'])

const form = ref({
  name: '',
  icon: '📝',
  sort: 99
})

const filteredCategories = computed(() => {
  return categories.value.filter(cat => cat.type === activeType.value)
})

const defaultCategories = computed(() => {
  return filteredCategories.value.filter(cat => {
    const isDefault = cat.isDefault !== undefined ? cat.isDefault : cat.is_default
    return isDefault === 1
  })
})

const customCategories = computed(() => {
  return filteredCategories.value.filter(cat => {
    const isDefault = cat.isDefault !== undefined ? cat.isDefault : cat.is_default
    return isDefault === 0
  })
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

const editCategory = (category) => {
  editId.value = category.id
  form.value = {
    name: category.name,
    icon: category.icon,
    sort: category.sort
  }
  showEditModal.value = true
}

const deleteCategory = async (category) => {
  if (!confirm('确定要删除这个分类吗？')) return
  try {
    await request.post('/category/delete', {
      userId: user.value.id,
      id: category.id
    })
    loadCategories()
  } catch (error) {
    alert(error.message)
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editId.value = null
  form.value = {
    name: '',
    icon: '📝',
    sort: 99
  }
}

const handleSubmit = async () => {
  if (!form.value.name.trim()) {
    alert('请输入分类名称')
    return
  }

  try {
    if (showAddModal.value) {
      await request.post('/category/add', {
        userId: user.value.id,
        name: form.value.name,
        type: activeType.value,
        icon: form.value.icon,
        sort: form.value.sort
      })
    } else {
      await request.post('/category/update', {
        userId: user.value.id,
        id: editId.value,
        name: form.value.name,
        icon: form.value.icon,
        sort: form.value.sort
      })
    }
    closeModal()
    loadCategories()
  } catch (error) {
    alert(error.message)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.categories-container {
  max-width: 800px;
  margin: 0 auto;
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

.section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin-bottom: 0;
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

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.category-item {
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.category-item.default {
  background: #f9f9f9;
}

.category-item .icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-item .name {
  font-size: 14px;
  color: #333;
}

.category-item .tag {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  color: #999;
  background: #eee;
  padding: 2px 6px;
  border-radius: 4px;
}

.category-item .actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
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

.empty {
  padding: 40px;
  text-align: center;
  color: #999;
  background: white;
  border-radius: 8px;
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
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
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

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.icon-selector {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 8px;
}

.icon-option {
  font-size: 28px;
  padding: 8px;
  text-align: center;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-option:hover {
  border-color: #667eea;
}

.icon-option.selected {
  border-color: #667eea;
  background: #f0f2ff;
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
