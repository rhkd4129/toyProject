<script setup>
import {ref} from "vue";


const menuItems = ref([
  { title: 'Home', to: '/', icon: 'mdi-home' },
  { title: 'PDF', to: '/pdf/upload', icon: 'mdi-information' },
  // { title: 'Coloring', to: '/color', icon: 'mdi-information' },
  // { title: 'MyColoring', to: '/MyColor', icon: 'mdi-information' },

])


</script>

<template>
  <v-app-bar app color="primary">
    <v-container class="d-flex align-center" fluid>
      <v-row no-gutters>
        <!-- 로고/타이틀 (왼쪽) -->
        <v-col cols="3">
          <v-app-bar-title>My App</v-app-bar-title>
        </v-col>

        <!-- 네비게이션 메뉴 (가운데) -->
        <v-col cols="6" class="d-flex justify-center">
          <v-btn
              v-for="item in menuItems"
              :key="item.title"
              :to="item.to"
              variant="text"
              class="mx-2"
          >
            {{ item.title }}
          </v-btn>
        </v-col>

        <!-- 로그인 버튼 (오른쪽) -->
<!--        <v-col cols="3" class="d-flex justify-end">-->
<!--          <v-btn variant="text">로그인</v-btn>-->
<!--        </v-col>-->
      </v-row>
    </v-container>
  </v-app-bar>

  <!-- 로그인 모달 컴포넌트 -->

</template>

<style scoped>
</style>
