<template>
  <!-- 上传器 -->
  <uploader
    ref="uploader"
    :options="options"
    :autoStart="false"
    :file-status-text="fileStatusText"
    @file-added="onFileAdded"
    @file-success="onFileSuccess"
    @file-progress="onFileProgress"
    @file-error="onFileError"
    class="uploader-ui"
  >
    <uploader-unsupport></uploader-unsupport>
    <uploader-drop>
      <div>
        <uploader-btn id="global-uploader-btn" :attrs="attrs" ref="uploadBtn"
          >选择文件<i class="el-icon-upload el-icon--right"></i
        ></uploader-btn>
      </div>
    </uploader-drop>
    <uploader-list></uploader-list>
  </uploader>
</template>

<script>
import { ACCEPT_CONFIG } from '@/utils/fileConfig'
import SparkMD5 from 'spark-md5'
import { mergeFile } from '@/api/file'
import { getToken } from '@/utils/auth'
export default {
  data() {
    return {
      options: {
        //目标上传 URL，默认POST
        target:
          process.env.VUE_APP_BASE_API + '/business/resource/upload/chunk',
        //分块大小(单位：字节)
        chunkSize: '2048000',
        //上传文件时文件内容的参数名，对应chunk里的Multipart对象名，默认对象名为file
        fileParameterName: 'uploadFile',
        //失败后最多自动重试上传次数
        maxChunkRetries: 3,
        //是否开启服务器分片校验，对应GET类型同名的target URL
        testChunks: true,
        headers: { Authorization: 'Bearer ' + getToken() },
        /*
         服务器分片校验函数，判断秒传及断点续传,传入的参数是Uploader.Chunk实例以及请求响应信息
         reponse码是successStatuses码时，才会进入该方法
         reponse码如果返回的是permanentErrors 中的状态码，不会进入该方法，直接进入onFileError函数 ，并显示上传失败
         reponse码是其他状态码，不会进入该方法，正常走标准上传
         checkChunkUploadedByResponse函数直接return true的话，不再调用上传接口
         */
        checkChunkUploadedByResponse: function (chunk, response_msg) {
          let objMessage = JSON.parse(response_msg)
          if (objMessage.skipUpload) {
            return true
          }
          return (
            (objMessage.uploadedChunks || []).indexOf(chunk.offset + 1) >= 0
          )
        },
      },
      attrs: {
        accept: ACCEPT_CONFIG.getAll(),
      },
      fileStatusText: {
        success: '上传成功',
        error: '上传失败',
        uploading: '上传中',
        paused: '暂停',
        waiting: '等待上传',
      },
    }
  },
  methods: {
    onFileAdded(file) {
      this.computeMD5(file)
    },
    /*
    第一个参数 rootFile 就是成功上传的文件所属的根 Uploader.File 对象，它应该包含或者等于成功上传文件；
    第二个参数 file 就是当前成功的 Uploader.File 对象本身；
    第三个参数就是 message 就是服务端响应内容，永远都是字符串；
    第四个参数 chunk 就是 Uploader.Chunk 实例，它就是该文件的最后一个块实例，如果你想得到请求响应码的话，chunk.xhr.status就是
    */
    onFileSuccess(rootFile, file, response, chunk) {
      console.log(rootFile, file, response, chunk)
      //refProjectId为预留字段，可关联附件所属目标，例如所属档案，所属工程等
      file.refProject = '123456789'
      mergeFile(JSON.stringify(file))
        .then(res => {
          let { code, msg, data } = res
          if (code === 200) {
            this.$emit('uploadFile', data)
          } else {
            this.msgError('合并文件出错：' + msg)
          }
        })
        .catch(error => {
          console.log('合并后捕获的未知异常：' + error)
        })
    },
    // 文件进度的回调
    onFileProgress(rootFile, file, chunk) {
      console.log(
        `上传中 ${file.name}，chunk：${chunk.startByte / 1024 / 1024} ~ ${
          chunk.endByte / 1024 / 1024
        }`
      )
    },
    onFileError(rootFile, file, response, chunk) {
      console.log('上传完成后异常信息：' + response)
    },

    /**
     * 计算md5，实现断点续传及秒传
     * @param file
     */
    computeMD5(file) {
      file.pause()
      //单个文件的大小限制2G
      let fileSizeLimit = 2 * 1024 * 1024 * 1024
      console.log('文件大小：' + file.size)
      console.log('限制大小：' + fileSizeLimit)
      if (file.size > fileSizeLimit) {
        this.$message({
          showClose: true,
          message: '文件大小不能超过2G',
        })
        file.cancel()
      }

      let fileReader = new FileReader()
      let time = new Date().getTime()
      let blobSlice =
        File.prototype.slice ||
        File.prototype.mozSlice ||
        File.prototype.webkitSlice
      let currentChunk = 0
      const chunkSize = 10 * 1024 * 1000
      let chunks = Math.ceil(file.size / chunkSize)
      let spark = new SparkMD5.ArrayBuffer()
      //由于计算整个文件的Md5太慢，因此采用只计算第1块文件的md5的方式
      let chunkNumberMD5 = 1

      loadNext()

      fileReader.onload = e => {
        spark.append(e.target.result)

        if (currentChunk < chunkNumberMD5) {
          loadNext()
        } else {
          let md5 = spark.end()
          file.uniqueIdentifier = md5
          file.resume()
          console.log(
            `MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${
              file.size
            } 用时：${new Date().getTime() - time} ms`
          )
        }
      }

      fileReader.onerror = function () {
        this.error(`文件${file.name}读取出错，请检查该文件`)
        file.cancel()
      }

      function loadNext() {
        let start = currentChunk * chunkSize
        let end = start + chunkSize >= file.size ? file.size : start + chunkSize

        fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end))
        currentChunk++
        console.log('计算第' + currentChunk + '块')
      }
    },
    close() {
      this.$refs.uploader.fileRemoved()
    },
    error(msg) {
      this.$notify({
        title: '错误',
        message: msg,
        type: 'error',
        duration: 2000,
      })
    },
  },
}
</script>

<style>
.uploader-ui {
  padding: 15px;
  margin: 40px auto 0;
  font-size: 12px;
  font-family: Microsoft YaHei;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.4);
}
.uploader-ui .uploader-btn {
  margin-right: 4px;
  font-size: 12px;
  border-radius: 3px;
  color: #fff;
  background-color: #409eff;
  border-color: #409eff;
  display: inline-block;
  line-height: 1;
  white-space: nowrap;
}
.uploader-ui .uploader-list {
  max-height: 440px;
  overflow: auto;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
