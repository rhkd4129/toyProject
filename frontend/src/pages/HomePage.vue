<script setup>
import HelloWorld from '@components/HelloWorld.vue'
import {ref} from "vue";
import {getData} from "@api";

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
