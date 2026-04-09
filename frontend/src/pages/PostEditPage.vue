<script setup>
import { onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import PostForm from "@/components/post/PostForm.vue";

const store = useStore();
const route = useRoute();

onMounted(async () => {
  await store.dispatch("postStore/loadPost", route.params.id);
});
</script>

<template>
  <v-container>
    <PostForm :edit-mode="true" />
  </v-container>
</template>
