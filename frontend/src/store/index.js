import postStore from '@/store/post.js'
import { createStore } from "vuex";
const store = createStore({
    modules: {
        postStore
    }
})
export default store

// store.state.a // -> moduleA'의 상태
