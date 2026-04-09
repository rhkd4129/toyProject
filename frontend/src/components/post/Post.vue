<script setup>
import { computed } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

const store = useStore();
const router = useRouter();

const post = computed(() => store.getters["postStore/post"]);

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
  return new Date(dateStr).toLocaleString("ko-KR");
};
</script>

<template>
  <v-card v-if="post?.id" flat border>
    <v-card-item>
      <template #prepend>
        <v-chip
          :color="postTypeColor(post.postType?.postType)"
          size="small"
          variant="tonal"
          class="mr-2"
        >
          {{ postTypeLabel(post.postType?.postType) }}
        </v-chip>
      </template>
      <v-card-title class="text-h6">{{ post.title }}</v-card-title>
      <template #append>
        <span class="text-caption text-medium-emphasis">{{ formatDate(post.createdAt) }}</span>
      </template>
    </v-card-item>

    <v-divider />

    <v-card-text class="text-body-1" style="white-space: pre-wrap; min-height: 200px;">
      {{ post.content }}
    </v-card-text>

    <v-divider />

    <v-card-actions>
      <v-btn variant="tonal" color="blue" prepend-icon="mdi-thumb-up">
        {{ post.likes }}
      </v-btn>
      <v-btn variant="tonal" color="red" prepend-icon="mdi-thumb-down">
        {{ post.dislikes }}
      </v-btn>
      <v-spacer />
      <v-btn variant="text" @click="router.back()">목록으로</v-btn>
      <v-btn variant="tonal" color="primary" @click="router.push({ name: 'postEdit', params: { id: post.id } })">수정</v-btn>
    </v-card-actions>
  </v-card>

  <v-alert v-else type="info" variant="tonal" text="게시물을 찾을 수 없습니다." />
</template>
