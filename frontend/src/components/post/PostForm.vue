<script setup>
import { ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

const store = useStore();
const router = useRouter();

const form = ref(null);
const loading = ref(false);

const title = ref("");
const content = ref("");
const postType = ref(null);

const postTypeOptions = [
  { title: "공지사항", value: "NOTICE" },
  { title: "지식", value: "GENERAL" },
  { title: "Q&A", value: "QNA" },
];

const rules = {
  required: (v) => !!v || "필수 항목입니다.",
  minLength: (v) => (v && v.length >= 2) || "2자 이상 입력해주세요.",
};

const submit = async () => {
  const { valid } = await form.value.validate();
  if (!valid) return;

  loading.value = true;
  try {
    await store.dispatch("postStore/addPost", {
      title: title.value,
      content: content.value,
      postType: postType.value,
    });
    router.push({ name: "posts" });
  } finally {
    loading.value = false;
  }
};

const cancel = () => {
  router.push({ name: "posts" });
};
</script>

<template>
  <v-card flat border max-width="800">
    <v-card-title class="pa-4">게시물 작성</v-card-title>
    <v-divider />

    <v-card-text>
      <v-form ref="form" @submit.prevent="submit">
        <v-select
          v-model="postType"
          :items="postTypeOptions"
          label="분류"
          :rules="[rules.required]"
          class="mb-3"
        />

        <v-text-field
          v-model="title"
          label="제목"
          :rules="[rules.required, rules.minLength]"
          class="mb-3"
        />

        <v-textarea
          v-model="content"
          label="내용"
          :rules="[rules.required, rules.minLength]"
          rows="10"
          no-resize
        />
      </v-form>
    </v-card-text>

    <v-divider />

    <v-card-actions class="pa-4">
      <v-spacer />
      <v-btn variant="text" @click="cancel">취소</v-btn>
      <v-btn
        variant="tonal"
        color="primary"
        :loading="loading"
        @click="submit"
      >
        등록
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
