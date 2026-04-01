import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import store from '@/store/index.js'
import router from '@/router/index.js'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'


const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light',
        themes: {
            light: {
                colors: {
                    primary: '#1867C0',
                    secondary: '#5CBBF6',
                    accent: '#82B1FF',
                },
            },
        },
    },
})
const app = createApp(App)
app.use(store);
app.use(vuetify)
app.use(router)
app.mount('#app')
