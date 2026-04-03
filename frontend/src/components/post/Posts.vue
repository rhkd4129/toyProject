<script setup>
 // setup() → onBeforeMount() → onMounted()
 // setup은 dom도 없고 this도없다
 // onMounted()는 "이 함수를 나중에 실행해줘" 라고 예약하는 것이고,실제 실행은 DOM이 다 만들어진 후
 import {computed, onMounted} from "vue";
 import postStore from "@store/post.js";
 import {useStore} from "vuex";

 const store =useStore()
 // onMounted(async ()=>{
 //    await store.dispatch("postStore/loadPosts")
 // })

 const posts = computed(()=>store.getters['postStore/posts'])



</script>

<template>

  <div>
    <template v-if="posts.length !== 0">
      게시물 존재
    </template>
    <template v-else>
      <p> 게시물이 없습니다 </p>
    </template>
  </div>
</template>

<style scoped>

</style>
