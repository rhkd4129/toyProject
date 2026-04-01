<script setup>
import HelloWorld from './components/HelloWorld.vue'
import  {getData }from './api/index.js'
import {ref} from "vue";

const result = ref(null)
const fetchData = async () =>{
  result.value = await getData()
}
</script>




<template>
  <HelloWorld msg="Vite + Vue" />
  <button @click="fetchData">데이터 불러오기</button>
  <p>{{result}}</p>
</template>

<style scoped>
</style>
