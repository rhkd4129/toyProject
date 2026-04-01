import axios from 'axios';
export const apiClient  = axios.create({
    baseURL: 'http://localhost:8085/api',
    headers: {
        'Content-type': 'application/json',
    },
    withCredentials:true
});
export const getData = async ()=>{
    try{
        const response = await  apiClient.get("/")
        return response.data

    }catch(error){
        console.error('Error fetching PhotoList:', error);
        throw error;
    }
}
