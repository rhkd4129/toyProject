<script setup>

import  {getData }from '@/api/index.js'
import {ref} from "vue";
import PhotoForm from "@components/photoForm.vue";

const result = ref(null)
const fetchData = async () =>{
  result.value = await getData()
}
</script>




<template>

  <v-layout>
<!--    <Header />-->
    <v-main>
      <RouterView />
    </v-main>
  </v-layout>
<!--  <HelloWorld msg="Vite + Vue" />-->
<!--    <PhotoForm></PhotoForm>-->

<!--  <button @click="fetchData">데이터 불러오기</button>-->
<!--  <p>{{result}}</p>-->
</template>

<style scoped>
</style>
