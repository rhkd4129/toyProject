import postStore from '@/store/post.js'
import pdfStore from "@store/pdf.js";
import { createStore } from "vuex";
const store = createStore({
    modules: {
        postStore,
        pdfStore
    }
})
export default store

// store.state.a // -> moduleA'의 상태
