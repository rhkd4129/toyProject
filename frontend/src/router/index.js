import { createRouter, createWebHistory } from 'vue-router'
import PdfPage from "@pages/PdfPage.vue";
import HomePage from "@pages/HomePage.vue";

const routes = [
    {
        path: '/',
        name: 'homePage',
        component: HomePage
    },
    {
        path: '/posts',
        name: 'posts',
        component: () => import('@pages/PostsPage.vue')
    },
    {
        path: '/post/:id',
        name: 'post',
        component: () => import('@pages/PostPage.vue')
    },
    {
        path: '/post/create',
        name: 'postCreate',
        component: () => import('@pages/PostFormPage.vue')
    },
    {
        path: '/pdf/upload',
        name: 'pdfPage',
        component: PdfPage
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
