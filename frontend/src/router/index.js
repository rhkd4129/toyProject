import { createRouter, createWebHistory } from 'vue-router'
import Home from '@pages/HomePage.vue'
import PdfPage from "@pages/PdfPage.vue";
import HomePage from "@pages/HomePage.vue";
const routes = [

    {
        path: '/',
        name: 'homePage',
        component:HomePage
        // component: () => import('../views/HomeView.vue')  // lazy loading으로 변경
    },
    {
        path: '/pdf/upload',
        name: 'pdfPage',
        component:PdfPage
        // component: () => import('../views/HomeView.vue')  // lazy loading으로 변경


    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
