import {createPost, fetchPosts} from "@api";
import store from "@/store/index.js";

const postStore = {
    namespaced:true,
    state:()=>({
        posts:[],
        post:{}
    }),
    getters:{
        posts(state){return state.posts},
        post(state){return state.post}

    },
    mutations:{
        setPosts(state,posts){state.posts = posts},
        setPost(state,post){state.post = post}

    },
    actions:{
        async loadPosts({state,commit}){
            const posts = await fetchPosts()
            commit('postStore/setPosts',posts)

        },
        async addPost({state},newPost) {
            const result = await createPost(newPost)
        }

    }
}


export default postStore
