import {createPost, fetchPosts, fetchPost, updatePost} from "@api";

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
        async loadPosts({commit}){
            const posts = await fetchPosts()
            commit('setPosts', posts)
        },
        async loadPost({commit}, id){
            const post = await fetchPost(id)
            commit('setPost', post)
        },
        async addPost({state}, newPost) {
            const result = await createPost(newPost)
        },
        async editPost({state}, {id, post}) {
            await updatePost(id, post)
        }
    }
}


export default postStore
