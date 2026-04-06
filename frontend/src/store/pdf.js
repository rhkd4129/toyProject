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
            const result = await createPdf(value)
            // commit('setPdf',)response
        }
    }
}


export default pdfStore
