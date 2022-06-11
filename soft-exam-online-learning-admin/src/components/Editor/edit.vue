<template>
  <div>
    <div style="border: 1px solid #ccc; margin-top: 10px">
      <!-- 工具栏 -->
      <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editor"
        :defaultConfig="toolbarConfig"
      />
      <!-- 编辑器 -->
      <Editor
        style="height: 200px"
        :defaultConfig="editorConfig"
        v-model="editContent"
        @onChange="onChange"
        @onCreated="onCreated"
      />
    </div>
  </div>
</template>

<script>
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

export default {
  name: "wangEditor",
  components: { Editor, Toolbar },
  props: {
    placeholder: {
      type: String,
      default: "",
    },
    upload: {
      type: Object,
      default: () => ({}),
    },
    content: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      editor: null,
      editContent: "",
      toolbarConfig: {
        // toolbarKeys: [ /* 显示哪些菜单，如何排序、分组 */ ],
        // excludeKeys: [ /* 隐藏哪些菜单 */ ],
      },
      editorConfig: {
        placeholder: this.placeholder,
        // 所有的菜单配置，都要在 MENU_CONF 属性下
        MENU_CONF: {},
      },
    };
  },
  methods: {
    onCreated(editor) {
      this.editContent = this.content;
      this.editor = Object.seal(editor); // 【注意】一定要用 Object.seal() 否则会报错
    },
    onChange(editor) {
      this.$emit("changeContent", editor.getHtml());
    },
    disableHandler() {
      const editor = this.editor;
      if (editor == null) return;
      editor.disable();
    },
    onUploadImgSuccess(res) {
      console.log("000", res);
    },
    clearEditorContent() {
      this.editContent = this.placeholder;
    },
  },
  created() {
    this.editorConfig.MENU_CONF["uploadImage"] = {
      server: this.upload?.api,
      // form-data fieldName ，默认值 'wangeditor-uploaded-image'
      fieldName: this.upload?.name,
      // 单个文件的最大体积限制，默认为 2M
      maxFileSize: 1 * 1024 * 1024, // 1M
      // 最多可上传几个文件，默认为 100
      maxNumberOfFiles: 10,
      // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
      allowedFileTypes: ["image/*"],
      // 自定义上传参数，例如传递验证的 token 等。参数会被添加到 formData 中，一起上传到服务端。
      meta: {
        type: this.upload?.dataType.type,
      },
      withCredentials: true,
      // 将 meta 拼接到 url 参数中，默认 false
      metaWithUrl: false,
      // 自定义增加 http  header
      headers: {
        Accept: "application/json;utf-8",
        Authorization: this.upload?.headers.Authorization,
      },
      // 单个文件上传成功之后
      onSuccess(file, res) {
        console.log(`${file.name} 上传成功`, res);
      },
      // 上传错误，或者触发 timeout 超时
      onError(file, err, res) {
        console.log(`${file.name} 上传出错`, err, res);
      },
    };
  },
  watch: {
    content: {
      handler(val) {
        console.log(val);
        this.editContent = val;
      },
    },
  },
  beforeDestroy() {
    const editor = this.editor;
    if (editor == null) return;
    editor.destroy(); // 组件销毁时，及时销毁 editor ，重要！！！
  },
};
</script>

<style src="@wangeditor/editor/dist/css/style.css"></style>
