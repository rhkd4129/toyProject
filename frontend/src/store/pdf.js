import {conEmitter, createPdf, createPdfTaskId, downloadPdf} from "@api";
import {standardEasing} from "vuetify/lib/util/index.d.ts";


const pdfStore={
    namespaced :true,

    state:()=>({
        pdf:null,
        pdfTaskId :null,
        pdfResultPath: null,  // 추가
    }),
    getters:{
        pdf(state){return state.pdf},
        pdfTaskId(state){return state.pdfTaskId}
    },
    mutations:{
        setPdf(state,value){state.pdf = value},
        setPdfTaskId(state,value){state.pdfTaskId  = value},
        setResultPath(state, path) {   state.pdfResultPath = path;}

    },

    actions:{

        async getPdf({state,dispatch},value){
            const response = await downloadPdf(value)
            console.log(response)


            const filenameMatch = String(response.headers['content-disposition'] ?? '') && String(response.headers['content-disposition'] ?? '').match(/filename="(.+)"/);
            const fileName = filenameMatch ? decodeURIComponent(filenameMatch[1]) : 'downloaded_file';
            const blobUrl = URL.createObjectURL(response.data);
            const pdf={
                blobUrl: blobUrl,
                fileName: fileName
            }
            return pdf;

        },

        async getPdfTaskId({commit}){
            const result = await createPdfTaskId();

            // result.data.pdfTaskId
            commit("setPdfTaskId", result.data)
        },

        async addPdf({state,commit},value) {
            const formData = new FormData();
            formData.append("newPdf", value.pdfFile); // JSON.stringify 제
            formData.append("pdfTaskId",state.pdfTaskId)
            const result = await createPdf(formData)
            console.log(result)
        },
        async connectEmitter({ state, commit }) {
            const es = conEmitter(state.pdfTaskId);

            es.addEventListener(import.meta.env.VITE_SSE_EVENT_CONNECT, (event) => {
                console.log("연결확인:", event.data);
            });

            es.addEventListener(import.meta.env.VITE_SSE_EVENT_PDF_DONE, (event) => {
                console.log("완료:", event.data);
                const { taskId, filePath } = JSON.parse(event.data);
                commit("setResultPath", taskId);  // 결과경로 저장
                es.close();                            // 연결 끊기
            });

            es.onerror = (error) => {
                console.error("SSE 에러:", error);
                es.close();
            };
        },

            // {
            //     "message": null,
            //     "data": {
            //     "taskId": "a6bb7471-a697-485c-b680-d172290afbe4",
            //         "filePath": "C:\\Server\\PDF\\a6bb7471-a697-485c-b680-d172290afbe4.pdf",
            //         "originalFileName": "20260428132839.pdf"
            // }
            // }

    }
}


export default pdfStore
