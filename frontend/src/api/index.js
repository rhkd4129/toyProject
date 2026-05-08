import axios from 'axios';
export const apiClient  = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-type': 'application/json',
    },
    withCredentials:true
});

export const apiFileClient  = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-type': 'multipart/form-data'
    },
    withCredentials:true
});

export async function createPost(newPost){
    try{
        const response = await apiClient.post("/post/create",newPost)
        return response.data
    }catch (error){
        console.error('Error create post:', error);
        throw error;
    }
}
export async function fetchPosts(){
    try{
        const response = await apiClient.get("/post/list")
        return response.data
    }catch (error){
        console.error('Error fetching posts:', error);
        throw error;
    }
}

export async function fetchPost(id){
    try{
        const response = await apiClient.get(`/post/list/${id}`)
        return response.data
    }catch (error){
        console.error('Error fetching post:', error);
        throw error;
    }
}

export async function updatePost(id, updatedPost){
    try{
        const response = await apiClient.put(`/post/${id}`, updatedPost)
        return response.data
    }catch (error){
        console.error('Error update post:', error);
        throw error;
    }
}

export async function createPdfTaskId(){
    try{
        const response = await apiClient.get("/pdf/create/id")
        return response.data
    }catch (error){
        console.error('Error fetching posts:', error);
        throw error;
    }
}


export async function createPdf(formData){
    try{
        const response  = await apiFileClient.post('/pdf/create',formData)
        return response.data
    }catch(error){
        console.error('Error createPdf:', error);
        throw error;
    }
}
export async function downloadPdf(taskId){
    try{
        const response  = await apiClient.get(`/pdf/download/${taskId}`,{
            responseType: 'blob'
        })

        return response
    }catch(error){
        console.error('Error createPdf:', error);
        throw error;
    }
}

export function conEmitter(pdfTaskId) {
    return new EventSource(`${import.meta.env.VITE_API_BASE_URL}/pdf/subscribe/${pdfTaskId}`);
}

