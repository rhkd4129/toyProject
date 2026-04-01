import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
// export default defineConfig({
export default ({ mode }) => {
  const isDevelop = mode === 'development';
  return defineConfig({
    plugins: [vue()],
    resolve: {
      alias: {
        // __dirname 현재 이 파일이 위치한 폴더의 절대 경로
        // path.resolve()경로 합치기
        '@': path.resolve(__dirname, './src'),
        '@pages': path.resolve(__dirname, './src/pages'),
        '@layouts': path.resolve(__dirname, './src/layouts'),
        // '@css': path.resolve(__dirname, './src/css'),
        '@api': path.resolve(__dirname, './src/api'),
        '@components': path.resolve(__dirname, './src/components'),
        '@store': path.resolve(__dirname, './src/store'),
        // '@Utils': path.resolve(__dirname, './src/utils'),
        // '@Hooks': path.resolve(__dirname, './src/hooks'),
        // '@Fetch': path.resolve(__dirname, './src/redux/fetch'),
        '@router': path.resolve(__dirname, './src/routes'),
        // '@Module': path.resolve(__dirname, './src/module'),
        // '@Model': path.resolve(__dirname, './src/model'),
      }
    },
    server: {
      port: 3000,
    },
  })
}
