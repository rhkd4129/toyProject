import {createPdf} from "@api";


const pdfStore={
    namespaced :true,

    state:()=>({
        pdf:null
    }),
    getters:{
        pdf(state){return state.pdf}
    },
    mutations:{
        setPdf(state,value){state.pdf = value}
    },
    actions:{
        async addPdf({state,commit},value){
            const formData = new FormData();
            formData.append("newPdf", value); // JSON.stringify 제
            const result = await createPdf(formData)
            console.log(result)

            // commit('setPdf',)response
        }
    }
}


export default pdfStore
