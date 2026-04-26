<template>
  <div class="statistics-container">
    <div class="chart-section">
      <div class="section-header">
        <h3 class="section-title">当月支出分类占比</h3>
        <input v-model="pieMonth" type="month" @change="loadPieData" class="month-select" />
      </div>
      <div ref="pieChartRef" class="chart"></div>
    </div>

    <div class="chart-section">
      <div class="section-header">
        <h3 class="section-title">近6个月收支趋势</h3>
      </div>
      <div ref="lineChartRef" class="chart"></div>
    </div>

    <div class="data-list" v-if="pieData.length > 0">
      <h3 class="section-title">支出明细</h3>
      <div class="list-item" v-for="(item, index) in pieData" :key="index">
        <div class="item-left">
          <span class="color-dot" :style="{ backgroundColor: pieColors[index % pieColors.length] }"></span>
          <span class="item-icon">{{ item.icon || '📝' }}</span>
          <span class="item-name">{{ item.name }}</span>
        </div>
        <div class="item-right">
          <span class="item-amount">¥{{ item.total_amount }}</span>
          <span class="item-percent">{{ getPercent(item.total_amount) }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'
import dayjs from '../utils/dayjs'

const user = computed(() => {
  const userData = localStorage.getItem('user')
  return userData ? JSON.parse(userData) : null
})

const pieMonth = ref(dayjs().format('YYYY-MM'))
const pieChartRef = ref(null)
const lineChartRef = ref(null)
let pieChart = null
let lineChart = null

const pieData = ref([])
const lineData = ref([])

const pieColors = ['#667eea', '#764ba2', '#f093fb', '#4facfe', '#00f2fe', '#43e97b', '#38f9d7', '#fa709a', '#fee140', '#30cfd0']

const totalAmount = computed(() => {
  return pieData.value.reduce((sum, item) => sum + (Number(item.total_amount) || 0), 0)
})

const getPercent = (amount) => {
  if (totalAmount.value === 0) return 0
  return ((Number(amount) / totalAmount.value) * 100).toFixed(1)
}

const loadPieData = async () => {
  if (!user.value) return
  try {
    const [year, month] = pieMonth.value.split('-').map(Number)
    const res = await request.get('/statistics/expense-by-category', {
      params: {
        userId: user.value.id,
        year,
        month
      }
    })
    pieData.value = res.data || []
    nextTick(() => {
      updatePieChart()
    })
  } catch (error) {
    console.error('加载饼图数据失败:', error)
  }
}

const loadLineData = async () => {
  if (!user.value) return
  try {
    const res = await request.get('/statistics/monthly-trend', {
      params: {
        userId: user.value.id,
        months: 6
      }
    })
    lineData.value = res.data || []
    nextTick(() => {
      updateLineChart()
    })
  } catch (error) {
    console.error('加载折线图数据失败:', error)
  }
}

const updatePieChart = () => {
  if (!pieChart) return
  
  const chartData = pieData.value.map((item, index) => ({
    name: item.name,
    value: Number(item.total_amount) || 0,
    itemStyle: { color: pieColors[index % pieColors.length] }
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '支出',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData
      }
    ]
  }

  pieChart.setOption(option)
}

const updateLineChart = () => {
  if (!lineChart) return
  
  const months = lineData.value.map(item => item.month_label)
  const income = lineData.value.map(item => Number(item.income) || 0)
  const expense = lineData.value.map(item => Number(item.expense) || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        let result = params[0].axisValue + '<br/>'
        params.forEach(item => {
          result += item.marker + item.seriesName + ': ¥' + item.value + '<br/>'
        })
        return result
      }
    },
    legend: {
      data: ['收入', '支出'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '40px',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#52c41a'
        },
        data: income
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 77, 79, 0.3)' },
            { offset: 1, color: 'rgba(255, 77, 79, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#ff4d4f'
        },
        data: expense
      }
    ]
  }

  lineChart.setOption(option)
}

const initCharts = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
  }
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
  }

  const handleResize = () => {
    pieChart?.resize()
    lineChart?.resize()
  }

  window.addEventListener('resize', handleResize)

  return () => {
    window.removeEventListener('resize', handleResize)
    pieChart?.dispose()
    lineChart?.dispose()
  }
}

onMounted(() => {
  const cleanup = initCharts()
  loadPieData()
  loadLineData()

  onUnmounted(() => {
    cleanup()
  })
})
</script>

<style scoped>
.statistics-container {
  max-width: 900px;
  margin: 0 auto;
}

.chart-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  margin: 0;
}

.month-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.chart {
  height: 350px;
  width: 100%;
}

.data-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.data-list .section-title {
  margin-bottom: 16px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.list-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.item-icon {
  font-size: 20px;
}

.item-name {
  font-size: 14px;
  color: #333;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.item-amount {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.item-percent {
  font-size: 12px;
  color: #999;
  min-width: 50px;
  text-align: right;
}
</style>
