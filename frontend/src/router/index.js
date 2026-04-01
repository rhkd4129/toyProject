import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/Home.vue'
const routes = [

    {
        path: '/',
        name: 'home',
        component:Home
        // component: () => import('../views/HomeView.vue')  // lazy loading으로 변경


    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
