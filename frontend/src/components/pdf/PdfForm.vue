<script setup>
import { reactive, ref } from "vue";
import {useStore} from "vuex";

const valid = ref(false);
const store = useStore()
const formData = ref({pdfFile: null,});


const rules = reactive({
  pdfFile: [
    v => !!v || 'pdf를 선택해주세요',
    // v => !v || v.size < 10000000 || '이미지 크기는 10MB 이하여야 합니다'
  ]
});

const submitForm = async ()=>{
  try{
    await store.dispatch("pdfStore/addPdf",formData.value)
  }catch (error){

  }

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
                  :rules="rules.pdfFile"
                  label="이미지 업로드"
                  accept="image/*"
                  prepend-icon="mdi-camera"
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
    </VContainer>
</template>

<style scoped>

</style>

