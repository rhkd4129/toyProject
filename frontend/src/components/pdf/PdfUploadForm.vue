<script setup>
import {computed, reactive, ref, watch} from "vue";

const props = defineProps({
  rules: {
    type: Array,
    default: () => [v => !!v || 'pdf를 선택해주세요']
  },
  submitLabel:{
    type:String,
    default:"생성하기"
  },
  loading: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits('file-selected','submit')
const valid = ref(false);
const formData = ref({ pdfFile: null });

const onFileSelected = (file) => {
  if (!file) return;
  emit('file-selected', file); // 부모가 알아서 처리
};

const submitForm = ()=>{
  emit('submit',formData.value)
}

const resetForm = () => {
  formData.value.pdfFile = null;
  valid.value = false;
};
</script>

<template>
  <VContainer>
    <v-form v-model="valid" @submit.prevent="submitForm">
      <v-card class="pa-4">
        <v-card-title class="text-h5 mb-4"></v-card-title>
        <v-file-input
            v-model="formData"
            @change="onFileSelected"
            :rules="props.rules"
            label="PDF 업로드"
            accept="application/pdf"
            prepend-icon="mdi-file-pdf-box"
            outlined
            dense
        ></v-file-input>


        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" type="submit" :disabled="!valid" :loading="loading">
            {{ submitLabel }}
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
  </VContainer>
</template>

<style scoped>

</style>
