import axios from 'axios';
export const apiClient  = axios.create({
    baseURL: 'http://localhost:8085/api',
    headers: {
        'Content-type': 'application/json',
    },
    withCredentials:true
});
export async function createPost(newPost){
    try{
        const response = await apiClient.post("/post/create",{"newPost":newPost})
        return response.data
    }catch (error){
        console.error('Error create post:', error);
        throw error;
    }
}
export async  function fetchPosts(){
    try{
        const response = await apiClient.post("/posts",{"newPost":newPost})
        return response.data
    }catch (error){
        console.error('Error create post:', error);
        throw error;
    }
}


export const fetchData = async ()=>{
    try{
        const response = await  apiClient.get("/")
        return response.data

    }catch(error){
        console.error('Error fetching PhotoList:', error);
        throw error;
    }
}
