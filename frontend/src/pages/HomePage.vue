<script setup>
import HelloWorld from '@components/HelloWorld.vue'
import {ref} from "vue";
import {loadData} from "@api/index.js";
import Posts from "@components/post/Posts.vue";

const result = ref(null)
const fetchData = async () =>{
  result.value = await loadData()
}
</script>

<template>

    <HelloWorld msg="Vite + Vue" />
    <button @click="fetchData">데이터 불러오기</button>
    <p>{{result}}</p>

  <Posts/>

</template>

<style scoped>

</style>
