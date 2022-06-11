export const ACCEPT_CONFIG = {
  UPLOAD_SIZE: 200, // 压缩文件大小
  image: ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
  video: ['.mp4', '.rmvb', '.mkv', '.wmv', '.flv'],
  document: [
    '.doc',
    '.docx',
    '.xls',
    '.xlsx',
    '.ppt',
    '.pptx',
    '.pdf',
    '.txt',
    '.tif',
    '.tiff',
    '.rar',
    '.zip',
  ],
  getAll() {
    return [...this.image, ...this.video, ...this.document]
  },
}
