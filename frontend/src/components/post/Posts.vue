<script setup>
import { computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

const store = useStore();
const router = useRouter();

onMounted(async () => {
  await store.dispatch("postStore/loadPosts");
});

const posts = computed(() => store.getters["postStore/posts"]);

const headers = [
  { title: "번호", key: "id", align: "center", width: "80px" },
  { title: "분류", key: "postType", align: "center", width: "100px" },
  { title: "제목", key: "title", align: "start" },
  { title: "좋아요", key: "likes", align: "center", width: "80px" },
  { title: "싫어요", key: "dislikes", align: "center", width: "80px" },
  { title: "작성일", key: "createdAt", align: "center", width: "120px" },
];

const postTypeLabel = (type) => {
  const map = { NOTICE: "공지사항", GENERAL: "지식", QNA: "Q&A" };
  return map[type] || type;
};

const postTypeColor = (type) => {
  const map = { NOTICE: "warning", GENERAL: "info", QNA: "success" };
  return map[type] || "default";
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("ko-KR");
};

const goToPost = (item) => {
  router.push({ name: "post", params: { id: item.id } });
};
</script>
<!--    @click:row="(event, { item,index }) => goToPost(item)"-->
<template>
  <div class="d-flex justify-end mb-3">
    <v-btn color="primary" variant="tonal" @click="router.push({ name: 'postCreate' })">
      글 작성
    </v-btn>
  </div>

  <v-data-table
    :headers="headers"
    :items="posts"
    :items-per-page="10"
    no-data-text="게시물이 없습니다."
    hover
    @click:row="(_, { item }) => goToPost(item)"
  >
    <template #item.postType="{ item }">
      <v-chip :color="postTypeColor(item.postType?.postType)" size="small" variant="tonal">
        {{ postTypeLabel(item.postType?.postType) }}
      </v-chip>
    </template>

    <template #item.createdAt="{ item }">
      {{ formatDate(item.createdAt) }}
    </template>
  </v-data-table>
</template>
