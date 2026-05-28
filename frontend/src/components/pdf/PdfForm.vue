<script setup>
import {computed, reactive, ref, watch} from "vue";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

const valid = ref(false);
const store = useStore()
const router = useRouter();

const pdfTypeOptions = [
  { title: "XLSX생성기", value: "XLSX" },
  { title: "초본분리기", value: "DIVIDE" },
]
const pdfFile = ref(null);
const pdfType = ref(null);
const  pdfResultPath = computed(()=>store.state.pdfStore.pdfResultPath)
const pdfErrorMessage = computed(()=>store.getters["pdfStore/errorMessage"])
const rules = reactive({
  required: (v) => !!v || "필수 항목입니다.",
  pdfFile: [
    v => !!v || 'pdf를 선택해주세요',
    // v => !v || v.size < 10000000 || '이미지 크기는 10MB 이하여야 합니다'
  ]
});

const onFileSelected = async (file)=>{
  try{
    if(!file) return;
    await store.dispatch('pdfStore/getPdfTaskId')
    await store.dispatch('pdfStore/connectEmitter')
  }catch (e) {
      console.log(e)
  }

}

const submitForm = async ()=>{
  try{

    const pdf = {
      pdfFile: pdfFile.value,
      pdfType:pdfType.value
    }
    await store.dispatch("pdfStore/addPdf",pdf)
    pdfFile.value = null;
    // router.push({ name: "posts" });
  }catch (error){
    console.log(error)
  }

}
const resetForm = () => {
  pdfFile.value = null;
  valid.value = false;
};

// const downloadPdf = async ()=>{
//   const taskId= "a"
//   const pdf = await store.dispatch("pdfStore/getPdf",taskId)
//   const a = document.createElement('a')
//   a.href = pdf.blobUrl
//   a.download = pdf.fileName
//   a.click()
//   URL.revokeObjectURL(pdf.blobUrl)
// // 메모리 해제 (중요!)
// }
  watch(pdfResultPath, async(newVal, oldVal)  => {
    console.log(newVal, oldVal)
    const pdf = await store.dispatch("pdfStore/getPdf",newVal)
    const a = document.createElement('a')
    a.href = pdf.blobUrl
    a.download = pdf.fileName
    a.click()

    URL.revokeObjectURL(pdf.blobUrl)


  })

watch(pdfErrorMessage, async(newVal, oldVal)  => {
  console.log(newVal, oldVal)
  alert(newVal)
})


</script>

<template>
  <v-card flat border max-width="800">
    <v-card-title class="pa-4">생성</v-card-title>
    <v-divider />
      <v-card-text>
        <v-form v-model="valid" @submit.prevent="submitForm">
            <v-select v-model="pdfType"
            :items="pdfTypeOptions"
            :rules="[rules.required]"
            label="분류"
            class="mb-3"></v-select>


              <v-card class="pa-4">
                <v-card-title class="text-h5 mb-4"></v-card-title>
                <v-file-input
                    v-model="pdfFile"
                    @change="onFileSelected"
                    :rules="rules.pdfFile"
                    label="PDF 업로드"
                    accept="application/pdf"
                    prepend-icon="mdi-file-pdf-box"
                    outlined
                    dense
                ></v-file-input>


                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                      color="primary"
                      type="submit"
                      :disabled="!valid"
                  >
                    생성하기
                  </v-btn>
                  <v-btn
                      color="grey"
                      text
                      @click="resetForm"
                  >
                    초기화
                  </v-btn>
                </v-card-actions>
            </v-card>
        </v-form>
    </v-card-text>
  </v-card>
</template>

<style scoped>

</style>

