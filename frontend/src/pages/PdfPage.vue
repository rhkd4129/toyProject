<script setup>
import PdfForm from "@components/pdf/PdfForm.vue";
import PdfUploadForm from "@components/pdf/PdfUploadForm.vue";
import {useStore} from "vuex";
  const store = useStore()
  const myRules = [
    v => !!v || 'pdf를 선택해주세요',
    v => !v || v.size < 10000000 || '10MB 이하여야 합니다'
  ];

  const handleSubmit= async (formData)=>{
    await store.dispatch("pdfStore/addPdf",formData)
  }
  const handleFileSelected = async (file)=>{
    await store.dispatch('pdfStore/getPdfTaskId')
    await store.dispatch('pdfStore/connectEmitter')

  }
</script>

<template>
  <PdfForm/>
  <p>XLSX 생성하기 </p>
  <PdfUploadForm
      :rules = "myRules"
       @file-selected="handleFileSelected"
      @submit="handleSubmit"
      submit-label="XLSX 생성하기">
  </PdfUploadForm>
</template>

<style scoped>

</style>
