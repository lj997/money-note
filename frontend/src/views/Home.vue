<template>
  <div class="home-container">
    <header class="header">
      <div class="header-left">
        <span class="app-name">💰 记账工具</span>
      </div>
      <div class="header-right">
        <span class="user-name">{{ user?.nickname || user?.username }}</span>
        <button class="btn-logout" @click="handleLogout">退出</button>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <nav class="bottom-nav">
      <router-link to="/home/records" class="nav-item">
        <span class="nav-icon">📋</span>
        <span class="nav-text">账单</span>
      </router-link>
      <router-link to="/home/add" class="nav-item">
        <span class="nav-icon">➕</span>
        <span class="nav-text">记账</span>
      </router-link>
      <router-link to="/home/statistics" class="nav-item">
        <span class="nav-icon">📊</span>
        <span class="nav-text">统计</span>
      </router-link>
      <router-link to="/home/budget" class="nav-item">
        <span class="nav-icon">💵</span>
        <span class="nav-text">预算</span>
      </router-link>
      <router-link to="/home/categories" class="nav-item">
        <span class="nav-icon">🏷️</span>
        <span class="nav-text">分类</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref(null)

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  }
})

const handleLogout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.app-name {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  font-size: 14px;
}

.btn-logout {
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-logout:hover {
  background: rgba(255, 255, 255, 0.3);
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.bottom-nav {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 8px 0;
  background: white;
  border-top: 1px solid #eee;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  text-decoration: none;
  color: #999;
  transition: color 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #667eea;
}

.nav-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.nav-text {
  font-size: 12px;
}
</style>
