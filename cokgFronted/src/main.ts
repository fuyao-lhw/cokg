import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '@/styles/global.css'
import 'element-plus/dist/index.css'
import '@/styles/admin-common.css' // 引入公共样式

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}


