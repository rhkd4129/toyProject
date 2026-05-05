import {conEmitter, createPdf, downloadPdf} from "@api";


const pdfStore={
    namespaced :true,

    state:()=>({
        pdf:null,
        pdfTaskId :null
    }),
    getters:{
        pdf(state){return state.pdf},
        pdfTaskId(state){return state.pdfTaskId}
    },
    mutations:{
        setPdf(state,value){state.pdf = value},
        setTaskId(state,value){state.pdfTaskId  = value}
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

        async addPdf({state,commit},value) {
            const formData = new FormData();
            formData.append("newPdf", value); // JSON.stringify 제
            const result = await createPdf(formData)
            commit("setTaskId", result.data.taskId)
        },
        async connectEmitter({state,commit}){

            const es =  conEmitter(state.pdfTaskId);
            es.addEventListener("connect", (event) => {
                console.log("연결확인:", event.data);


            });

            es.addEventListener("pdf완료", (event) => {
                console.log("완료:", event.data);
                // console.log(e.lastEventId)  // "123"  ← id
                commit("setTaskId",event.data)
            });

            es.onerror = (error) => {
                console.error("SSE 에러:", error);
                es.close();
            };
            // es.onmessage = (event) => {
            //     console.log('받은 데이터:', event.data);
            //     const data = JSON.parse(event.data);
            //     // commit("setPdfStatus", data);
            //
            //     if (data.status === "DONE") {
            //         es.close();
            //     }
            // };
        }

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
