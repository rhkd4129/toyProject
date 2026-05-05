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
export function conEmitter(pdfTaskId) {
    const url = `${import.meta.env.VITE_API_BASE_URL}/pdf/subscribe/${pdfTaskId}`;
    const eventSource = new EventSource(url);

    // eventSource.onmessage = (event) => {
    //     console.log('받은 데이터:', event.data);
    //
    //     // 여기서 상태 업데이트 등 처리
    // };
    //
    // eventSource.onerror = (error) => {
    //     console.error('SSE 에러:', error);
    //     eventSource.close();
    // };

    // 필요할 때 연결 종료할 수 있도록 반환
    return eventSource;
}

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

// export async function conEmitter(pdfTaskId){
//     try{
//         const response  = await apiFileClient.get('/pdf/subscribe/{pdfTaskId}')
//         return response.data
//     }catch(error){
//         console.error('Error createPdf:', error);
//         throw error;
//     }
// }
//
