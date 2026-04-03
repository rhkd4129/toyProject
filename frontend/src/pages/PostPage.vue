<script setup>
import { onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import Post from "@/components/post/Post.vue";

const store = useStore();
const route = useRoute();

onMounted(async () => {
  await store.dispatch("postStore/loadPost", route.params.id);
});
</script>

<template>
  <v-container>
    <Post />
  </v-container>
</template>
