<script setup>

import  {getData }from '@api/index.js'
import {ref} from "vue";
import Header from "@layouts/Header.vue";
import Footer from "@layouts/Footer.vue";

</script>

<template>
  <v-layout>
    <Header />
    <v-main>
      <RouterView />
    </v-main>
<!--    <Footer/>-->
  </v-layout>
<!--  <HelloWorld msg="Vite + Vue" />-->
<!--    <PhotoForm></PhotoForm>-->


</template>

<style scoped>
</style>
