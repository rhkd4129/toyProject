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

// 401 (인증 만료)
// 500, 503 등 서버 자체 오류
apiClient.interceptors.response.use(
    (response)=>{
        // const message = response.data.message
        // if(message !== null){
        //     console.log("메시지처리")
        // }
        // return response;
    },
    (error) => {
        const status = error.response?.status

        // if (status === 401) {
        //     store.dispatch('auth/logout')
        //     router.push('/login')
        //     return Promise.reject(error)
        // }

        if (status >= 500) {
            // toast.error('서버 오류가 발생했습니다')
            return Promise.reject(error)
        }

        // 400, 404, 409 등 비즈니스 에러는 그냥 통과
        return Promise.reject(error)
    }






)






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
export async function updateMetadata(formData){
    try{
        const response  = await apiFileClient.post('/pdf/updateMetaData',formData)
        return response.data
    }catch(error){
        console.error('Error createPdf:', error);
        throw error;
    }
}

export async function downloadPdf(fileName){
    try{
        const response  = await apiClient.get(`/pdf/download/${fileName}`,{
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

